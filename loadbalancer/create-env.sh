# adapt the following setting
resource_group=rg-lb-introduction-tds
region=germanywestcentral
username=adminuser
password='SecretPassword123!@#'
email=foo@bar.com
vnet_name=vnet-lb-weu-tds
vm_name=vm-lb-we-tds # max 14 digits
subnet_name=subnet-lb-westeu-tds
availability_set_name=as-lb-westeu-tds
network_security_group=nsg-vm-lb-westeu-tds
public_ip_name=pip-lb-tds

az group create -g $resource_group -l $region

az network public-ip create --name $public_ip_name --resource-group $resource_group

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
    -n $vm_name-0$NUM \
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
	  --nsg $network_security_group
done

for NUM in 1 2 3
do
  az vm open-port -g $resource_group --name $vm_name-0$NUM --port 80
done

for NUM in 1 2 3
do
  az vm extension set \
    --name CustomScriptExtension \
    --vm-name $vm_name-0$NUM \
    -g $resource_group \
    --publisher Microsoft.Compute \
    --version 1.8 \
    --settings '{"commandToExecute":"powershell Add-WindowsFeature Web-Server; powershell Add-Content -Path \"C:\\inetpub\\wwwroot\\Default.htm\" -Value $($env:computername)"}'
done

for NUM in 1 2 3
do
    az vm auto-shutdown \
    --resource-group $resource_group \
    --name $vm_name-0$NUM \
    --time 1830 \
    --email $email
done


