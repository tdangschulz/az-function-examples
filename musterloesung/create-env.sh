# adapt the following setting
resource_group=rg-lb-order-tds
region=germanywestcentral
username=adminuser
password='SecretPassword123!@#'
email=foo@bar.com
vnet_name=vnet-lb-weu-tds
vm_name=vm-lb-we-tds # max 14 digits
subnet_name=subnet-lb-westeu-tds
nat_gateway_name=nat-lb-wger-tds
availability_set_name=as-lb-westeu-tds
network_security_group=nsg-vm-lb-westeu-tds
public_ip_name=pip-lb-weu-tds
public_ip_lb_name=pip-config-weu-tds
loadbalance_name=lb-order-weu-tds
functionApp=func-weu-tds
functionsVersion="4"
storage="orderstoragetds"

az group create -g $resource_group -l $region

az network public-ip create --name $public_ip_name --resource-group $resource_group

az network public-ip create --name $public_ip_lb_name --resource-group $resource_group

az network nat gateway create --name $nat_gateway_name \
                              --resource-group $resource_group \
                              --location $region \
                              --public-ip-addresses $public_ip_name

az network vnet create \
  -n $vnet_name \
  -g $resource_group \
  -l $region \
  --address-prefixes '10.0.0.0/16' \
  --subnet-name $subnet_name \
  --subnet-prefixes '10.0.0.0/24'

  
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
    --image Ubuntu2204 \
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
  az vm open-port -g $resource_group --name $vm_name-0$NUM --port 8080 --priority 100
  az vm open-port -g $resource_group --name $vm_name-0$NUM --port 22 --priority 101
done

az network nsg rule create \
  --resource-group $resource_group \
  --nsg-name $network_security_group \
  --name AzureLoadBalancer \
  --priority 102 \
  --source-address-prefixes AzureLoadBalancer \
  --source-port-ranges "*" \
  --destination-address-prefixes "*" \
  --destination-port-ranges 8080 \
  --direction Inbound \
  --access Allow \
  --protocol Tcp \
  --description "Erlaubt eingehenden Datenverkehr vom Azure Load Balancer auf Port 8080"


for NUM in 1 2 3
do
    az vm auto-shutdown \
    --resource-group $resource_group \
    --name $vm_name-0$NUM \
    --time 1830 \
    --email $email
done

az network lb create --name $loadbalance_name \
                     --resource-group $resource_group \
                     --public-ip-address $public_ip_lb_name \
                     --frontend-ip-name frontend-ip 

az network lb address-pool create \
  --resource-group $resource_group \
  --lb-name $loadbalance_name \
  --name lb-backend-pool \
  --vnet $vnet_name \


# az network nic ip-config create \
#   --resource-group $resource_group \
#   --nic-name vm-lb-we-tds-01VMNic \
#   --name ipconfig1 \
#   --lb-name $loadbalance_name \
#   --vnet-name vnet_name

# az network nic ip-config create \
#   --resource-group $resource_group \
#   --nic-name vm-lb-we-tds-02VMNic \
#   --name ipconfig2 \
#   --lb-name $loadbalance_name \
#   --vnet-name vnet_name

# az network nic ip-config create \
#   --resource-group $resource_group \
#   --nic-name vm-lb-we-tds-03VMNic \
#   --name ipconfig3 \
#   --lb-name $loadbalance_name \
#   --vnet-name vnet_name    

az network lb probe create --lb-name $loadbalance_name  \
                           --name ssh-probe \
                           --port 22 \
                           --protocol Tcp \
                           --resource-group $resource_group
                    
az network lb rule create \
  --resource-group $resource_group \
  --lb-name $loadbalance_name \
  --name http-rule \
  --protocol tcp \
  --frontend-port 80 \
  --backend-port 8080 \
  --frontend-ip-name frontend-ip \
  --backend-pool-name lb-backend-pool \
  --probe-name ssh-probe  \
  --disable-outbound-snat true \
  --idle-timeout 15 \
  --enable-tcp-reset true

az network lb inbound-nat-rule create \
  --resource-group $resource_group \
  --lb-name $loadbalance_name \
  --name inbound-nat-rule \
  --protocol Tcp \
  --backend-port 22 \
  --frontend-ip-name frontend-ip \
  --backend-pool-name lb-backend-pool \
  --frontend-port-range-start 22 \
  --frontend-port-range-end 24

az network lb outbound-rule create --lb-name $loadbalance_name \
                                   --name outbound-lb-rule \
                                   --protocol Tcp \
                                   --resource-group $resource_group \
                                   --frontend-ip-configs frontend-ip \
                                   --backend-address-pool lb-backend-pool


for NUM in 1 2 3
do
az vm extension set \
  --resource-group $resource_group \
  --vm-name $vm_name-0$NUM \
  --name customScript \
  --publisher Microsoft.Azure.Extensions \
  --version 2.0 \
  --protected-settings '{"commandToExecute": "sudo apt-get update && sudo apt-get update && sudo apt-get -y upgrade && sudo apt install -y openjdk-17-jdk openjdk-17-jre"}'
done

az storage account create --name $storage --location $region --resource-group $resource_group --sku Standard_LRS

az functionapp create --name $functionApp \
                      --resource-group $resource_group \
                      --storage-account $storage \
                      --consumption-plan-location $region --functions-version $functionsVersion \
                      --os-type linux \
                      --runtime node \
                      --runtime-version 18

az servicebus namespace create --resource-group $resource_group \
                               --name bus-order-weu-tds \
                               --location $region \
                               --sku Standard

az servicebus topic create --resource-group $resource_group \
                           --namespace-name bus-order-weu-tds  \
                           --name cancelOrder

az servicebus topic subscription create \
  --resource-group $resource_group \
  --namespace-name bus-order-weu-tds \
  --topic-name cancelOrder \
  --name send_confirm_cancel

az servicebus topic subscription create \
  --resource-group $resource_group \
  --namespace-name bus-order-weu-tds \
  --topic-name cancelOrder \
  --name prcesses_order_state

az servicebus topic subscription create \
  --resource-group $resource_group \
  --namespace-name bus-order-weu-tds \
  --topic-name cancelOrder \
  --name report_cancels


