pragma solidity 0.4.21;

contract microgrid {
    
    // Constructor
    function microgrid() public {
        
    }
    
    // Smart meter object
    struct SmartMeter {
        uint id;
        address smart_meter_address;
        uint threshold;
        string name;
        uint energy_generated;
        uint energy_sold;
        uint current_balance;
        bool available;
        bool frozen;
    }
    
    // Logging events
    event SmartMeterRegistered(address smart_meter_address);
    event SmartMeterUpdated(address smart_meter_address);
    event SmartMeterThresholdUpdated(address smart_meter_address);
    event SmartMeterSynced(address smart_meter_address);
    event EnergyTransferred(address transferFrom, address transferTo, uint amount);

    // // Mapping/dictionary of kv pairs, and list of addresses stored in array
    // mapping(address => SmartMeter) public smartMeters;
    // address[] smartMeterList;

    mapping(uint => SmartMeter) public smartMeterList;
    uint smartMeterLength = 0;

    // Register new smart meter address
    // TODO: allow parameter for meter pool
    // function registerSmartMeter(address meterAddress, uint threshold) public {
    //     if(!checkExists(meterAddress)){
    //         smartMeterList.push(meterAddress);
    //         smartMeters[meterAddress].smart_meter_address = meterAddress;
    //         smartMeters[meterAddress].threshold = threshold;
    //         smartMeters[meterAddress].energy_generated = 0;
    //         smartMeters[meterAddress].energy_sold = 0;
    //         smartMeters[meterAddress].current_balance = 0;
    //         smartMeters[meterAddress].available = false;
    //         smartMeters[meterAddress].frozen = false;

    //         // SmartMeterRegistered(msg.sender);
    //     }
    // }

    function registerSmartMeter(uint meterNum, address meterAddress, uint threshold, string name) public {
        smartMeterList[meterNum].smart_meter_address = meterAddress;
        smartMeterList[meterNum].threshold = threshold;
        smartMeterList[meterNum].energy_generated = 0;
        smartMeterList[meterNum].energy_sold = 0;
        smartMeterList[meterNum].current_balance = 0;
        smartMeterList[meterNum].available = false;
        smartMeterList[meterNum].frozen = false;
        smartMeterLength++;

        emit SmartMeterRegistered(msg.sender);
    }

    // Producer actions
    // Update balances
    // function updateSmartMeter(address meter, uint energy_generated, uint energy_sold, uint current_balance) internal {
    //     if(!checkExists(meter)) {
    //         smartMeters[meter].energy_generated = energy_generated;
    //         smartMeters[meter].energy_sold = energy_sold;
    //         smartMeters[meter].current_balance = current_balance;
    //         if(smartMeters[meter].threshold < current_balance) {
    //             smartMeters[meter].available = true;
    //         }
    //         else {
    //             smartMeters[meter].available = false;
    //         }

    //         // SmartMeterUpdated(meter);
    //     }
    // }

    function updateAvailable(uint meterNum) internal {
        if(smartMeterList[meterNum].threshold < smartMeterList[meterNum].current_balance) {
            smartMeterList[meterNum].available = true;
        }
        else {
            smartMeterList[meterNum].available = false;
        }
    }
    
    function updateSmartMeter(uint meterNum, uint energy_generated, uint energy_sold, uint current_balance) public {
        smartMeterList[meterNum].energy_generated = energy_generated;
        smartMeterList[meterNum].energy_sold = energy_sold;
        smartMeterList[meterNum].current_balance = current_balance;
        updateAvailable(meterNum);

        emit SmartMeterUpdated(meter);
    }

    // Set threshold
    // function setSmartMeterThreshold(uint threshold) public {
    //     if(!checkExists(msg.sender)) {
    //         smartMeters[msg.sender].threshold = threshold;

    //         // SmartMeterThresholdUpdated(msg.sender);
    //     }
    // }

    function setSmartMeterThreshold(uint meterNum, uint threshold) public {
        require(smartMeterList[meterNum].smart_meter_address == msg.sender);
        smartMeterList[meterNum].threshold = threshold;

        emit SmartMeterThresholdUpdated(msg.sender);
    }

    // Buyer actions
    // Transfer energy credits
    // function transferEnergy(uint amount, address transferTo, address transferFrom) internal {
    //     if(smartMeters[transferFrom].available){
    //         updateSmartMeter(transferTo, smartMeters[transferTo].energy_generated, smartMeters[transferTo].energy_sold, (smartMeters[transferTo].current_balance + amount));
    //         updateSmartMeter(transferFrom, smartMeters[transferTo].energy_generated, smartMeters[transferTo].energy_sold + amount, (smartMeters[transferTo].current_balance - amount));
    //     }
    // }

    function transferEnergy(uint amount, uint transferTo, uint transferFrom) internal {
        if(smartMeterList[transferFrom].available){
            updateSmartMeter(transferTo, smartMeterList[transferTo].energy_generated, smartMeterList[transferTo].energy_sold, (smartMeterList[transferTo].current_balance + amount));
            updateSmartMeter(transferFrom, smartMeterList[transferFrom].energy_generated, (smartMeterList[transferFrom].energy_sold + amount), (smartMeterList[transferTo].current_balance - amount));
        }
    }

    // Iterate through available meteres until amount request is completed
    // function makeTransferRequest(address requestAddress, address[] buyFromAddresses, uint amount) internal {
    //     uint cur_amount = amount;
    //     for(uint i = 0; i < buyFromAddresses.length; i++) {
    //         uint difference = cur_amount - (smartMeters[buyFromAddresses[i]].current_balance - smartMeters[buyFromAddresses[i]].threshold);
    //         if (difference <= 0) {
    //             transferEnergy(cur_amount, requestAddress, buyFromAddresses[i]);
    //             // EnergyTransferred(requestAddress, buyFromAddresses[i], amount);
    //             return;
    //         }
    //         else {
    //             uint newamount = cur_amount - difference;
    //             transferEnergy((cur_amount - difference), requestAddress, buyFromAddresses[i]);
    //             // EnergyTransferred(requestAddress, buyFromAddresses[i], newamount);
    //             cur_amount = cur_amount - newamount;
    //         }
    //     }
    // }

    function makeTransferRequest(uint meterNum, uint[] meters, uint amount) internal {
        uint cur_amount = amount;
        for(uint i = 0; i < meters.length; i++) {
            if(smartMeterList[meters[i]].available){
                uint difference = cur_amount - (smartMeterList[meters[i]].current_balance - smartMeterList[meters[i]].threshold);
                if (difference <= 0) {
                    transferEnergy(cur_amount, meterNum, meters[i]);
                    // EnergyTransferred(requestAddress, buyFromAddresses[i], amount);
                    return;
                }
                else {
                    uint newamount = cur_amount - difference;
                    transferEnergy((cur_amount - difference), meterNum, meters[i]);
                    // EnergyTransferred(requestAddress, buyFromAddresses[i], newamount);
                    cur_amount = cur_amount - newamount;
                }
            }
        }

        // It not enough is available it falls through
    }

    // Run sync cycle
    // function syncMeters() public {
    //     address[] memory availableMeters = new address[](smartMeterList.length);
    //     address[] memory belowThreshold = new address[](smartMeterList.length);
    //     uint availableMetersCount = 0;
    //     uint belowThresholdCount = 0;

    //     for (uint i = 0; i < smartMeterList.length; i++){
    //         if(smartMeters[smartMeterList[i]].available){
    //             availableMeters[availableMetersCount] = (smartMeterList[i]);
    //             availableMetersCount++;
    //         }
    //         else {
    //             belowThreshold[belowThresholdCount] = (smartMeterList[i]);
    //             belowThresholdCount++;
    //         }
    //     }
    //     // Make list of available meters in pool
    //     // Make list of meters below threshold

    //     for (uint z = 0; z < belowThreshold.length; z++){
    //         uint amount = smartMeters[belowThreshold[z]].threshold - smartMeters[belowThreshold[z]].current_balance;
    //         makeTransferRequest(belowThreshold[z], availableMeters, amount);
    //         // SmartMeterSynced(belowThreshold[z]);
    //     }
    //     // Iterate through meters below threshold
    //     // Send buy request to first available meter
    //     // Check if amount in buy request greater than surplus over threshold
    //     // Get maximum possible and go to next available until amount fulfilled
    //     // Go to next in belowThreshold list if success
    // }

    function syncMeters() public {
        uint[] memory availableMeters = new uint[](smartMeterLength);
        uint[] memory belowThreshold = new uint[](smartMeterLength);
        
        uint availableMetersCount = 0;
        uint belowThresholdCount = 0;

        for (uint i = 0; i < smartMeterLength; i++){
            if(smartMeterList[i].available){
                availableMeters[availableMetersCount] = i;
                availableMetersCount++;
            }
            else {
                belowThreshold[belowThresholdCount] = i;
                belowThresholdCount++;
            }
        }
        // Make list of available meters in pool
        // Make list of meters below threshold

        if((availableMetersCount > 0) && (belowThresholdCount > 0)){
            for (uint z = 0; z < belowThreshold.length; z++){
                uint amount = smartMeterList[belowThreshold[z]].threshold - smartMeterList[belowThreshold[z]].current_balance;
                makeTransferRequest(belowThreshold[z], availableMeters, amount);
                // SmartMeterSynced(belowThreshold[z]);
            }
        }
        
        // Iterate through meters below threshold
        // Send buy request to first available meter
        // Check if amount in buy request greater than surplus over threshold
        // Get maximum possible and go to next available until amount fulfilled
        // Go to next in belowThreshold list if success
    }

    function getMeterAddress(uint meterNum) public returns(address) {
        return smartMeterList[meterNum].smart_meter_address;
    }
    
    function getThreshold(uint meterNum) public returns(uint) {
        return smartMeterList[meterNum].threshold;
    }

    function getName(uint meterNum) public returns(string) {
        return smartMeterList[meterNum].name;
    }

    function getEnergyGenerated(uint meterNum) public return(uint) {
        return smartMeterList[meterNum].energy_generated;
    }

    function getEnergySold(uint meterNum) public return(uint){
        return smartMeterList[meterNum].energy_sold;
    }

    function getCurrentBalance(uint meterNum) public return(uint){
        return smartMeterList[meterNum].current_balance;
    }

    function getAvailable(uint meterNum) public return(bool){
        return smartMeterList[meterNum].available;
    }

    // Internal function to check if user exists
    // function checkExists(address sender) view internal returns (bool){
    //     bool exists = false;

    //     for (uint i = 0; i < smartMeterList.length; i++){
    //         if(sender == smartMeterList[i]){
    //             exists = true;
    //         }
    //     }        

    //     return exists;
    // }
}