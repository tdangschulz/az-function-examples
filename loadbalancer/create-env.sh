# adapt the following setting
resource_group=rg-load-balancer-introduction-tuan
region=westeurope
username=adminuser
password='SecretPassword123!@#'
vnet_name=vnet-loadbalancer-westeu-tuan
subnet_name=subnet-loadbalancer-westeu-tuan
availability_set_name=as-loadbalancer-westeu-tuan

az group create -g $resource_group -l $region

az network vnet create \
  -n $vnet_name \
  -g $resource_group \
  -l $region \
  --address-prefixes '10.0.0.0/16' \
  --subnet-name $subnet_name \
  --subnet-prefixes '10.0.1.0/24'
  
az vm availability-set create \
  -n $availability_set_name \
  -l $region \
  -g $resource_group

for NUM in 1 2 3
do
  az vm create \
    -n vm-eu-0$NUM \
    -g $resource_group \
    -l $region \
    --size Standard_B2s \
    --image Win2019Datacenter \
    --admin-username $username \
    --admin-password $password \
    --vnet-name $vnet_name \
    --subnet $subnet_name \
    --public-ip-address "" \
    --availability-set $availability_set_name \
	  --nsg vm-nsg
done

for NUM in 1 2 3
do
  az vm open-port -g $resource_group --name vm-eu-0$NUM --port 80
done

for NUM in 1 2 3
do
  az vm extension set \
    --name CustomScriptExtension \
    --vm-name vm-loadbalancer-eu-0$NUM \
    -g $resource_group \
    --publisher Microsoft.Compute \
    --version 1.8 \
    --settings '{"commandToExecute":"powershell Add-WindowsFeature Web-Server; powershell Add-Content -Path \"C:\\inetpub\\wwwroot\\Default.htm\" -Value $($env:computername)"}'
done


