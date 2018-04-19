pragma solidity 0.4.21;

contract microgrid {
    
    // Constructor
    function microgrid() public {
        
    }
    
    // Smart meter object
    struct SmartMeter {
        int id;
        address smart_meter_address;
        int threshold;
        string name;
        int energy_generated;
        int energy_sold;
        int current_balance;
        bool available;
        // bool frozen;
    }
    
    // Logging events
    event SmartMeterRegistered(address smart_meter_address);
    event SmartMeterUpdated(address smart_meter_address);
    event SmartMeterThresholdUpdated(address smart_meter_address);
    event SmartMeterSynced(address smart_meter_address);
    event EnergyTransferred(address transferFrom, address transferTo, int amount);

    // // Mapping/dictionary of kv pairs, and list of addresses stored in array
    // mapping(address => SmartMeter) public smartMeters;
    // address[] smartMeterList;

    mapping(uint => SmartMeter) public smartMeterList;
    uint public smartMeterLength = 0;

    uint public availableMetersCount = 0;
    uint public belowThresholdCount = 0;

    // Register new smart meter address
    // TODO: allow parameter for meter pool
    // function registerSmartMeter(address meterAddress, int threshold) public {
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

    function registerSmartMeter(uint meterNum, address meterAddress, int threshold, string name) public {
        smartMeterList[meterNum].smart_meter_address = meterAddress;
        smartMeterList[meterNum].threshold = threshold;
        smartMeterList[meterNum].name = name;
        smartMeterList[meterNum].energy_generated = 0;
        smartMeterList[meterNum].energy_sold = 0;
        smartMeterList[meterNum].current_balance = 100000;
        smartMeterList[meterNum].available = false;
        // smartMeterList[meterNum].frozen = false;
        smartMeterLength++;

        emit SmartMeterRegistered(meterAddress);
        // emit SmartMeterRegistered(msg.sender);
    }

    // Producer actions
    // Update balances
    // function updateSmartMeter(address meter, int energy_generated, int energy_sold, int current_balance) internal {
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
    
    function updateSmartMeter(uint meterNum, int energy_generated, int energy_sold, int current_balance) internal {
        smartMeterList[meterNum].energy_generated = energy_generated;
        smartMeterList[meterNum].energy_sold = energy_sold;
        smartMeterList[meterNum].current_balance = current_balance;
        updateAvailable(meterNum);

        emit SmartMeterUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    function updateSmartMeterExt(uint meterNum, int energy_generated, int energy_consumed) public {
        smartMeterList[meterNum].energy_generated = smartMeterList[meterNum].energy_generated + energy_generated;
        smartMeterList[meterNum].current_balance = smartMeterList[meterNum].current_balance + energy_generated - energy_consumed;
        updateAvailable(meterNum);

        emit SmartMeterUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    function updateSmartMeters(int[] generation_updates, int[] consumption_updates) public {
        for (uint i = 0; i < smartMeterLength; i++){
            updateSmartMeterExt(i, generation_updates[i], consumption_updates[i]);
        }

        syncMeters();
    }

    // function updateSmartMetersBatch(int[][] updates) public {
    //     int[] memory generation_updates = new int[](9);
    //     int[] memory consumption_updates = new int[](9);

    //     for (int i = 0; i < updates.length; i++) {
    //         for(int x = 0; x < 10; x++) {
    //             generation_updates[x] = updates[i][x];
    //         }

    //         for(int y = 10; y < 20; y++) {
    //             consumption_updates[(y-10)] = updates[i][y];
    //         }

    //         updateSmartMeters(generation_updates, consumption_updates);
    //     }
    // }

    // Set threshold
    // function setSmartMeterThreshold(int threshold) public {
    //     if(!checkExists(msg.sender)) {
    //         smartMeters[msg.sender].threshold = threshold;

    //         // SmartMeterThresholdUpdated(msg.sender);
    //     }
    // }

    function setSmartMeterThreshold(uint meterNum, int threshold) public {
        // require(smartMeterList[meterNum].smart_meter_address == msg.sender);
        smartMeterList[meterNum].threshold = threshold;

        emit SmartMeterThresholdUpdated(msg.sender);
    }

    function setSmartMeterThresholdArray(int[] thresholds) public {
        for (uint i = 0; i < smartMeterLength; i++){
            setSmartMeterThreshold(i, thresholds[i]);
        }
    }

    // Buyer actions
    // Transfer energy credits
    // function transferEnergy(int amount, address transferTo, address transferFrom) internal {
    //     if(smartMeters[transferFrom].available){
    //         updateSmartMeter(transferTo, smartMeters[transferTo].energy_generated, smartMeters[transferTo].energy_sold, (smartMeters[transferTo].current_balance + amount));
    //         updateSmartMeter(transferFrom, smartMeters[transferTo].energy_generated, smartMeters[transferTo].energy_sold + amount, (smartMeters[transferTo].current_balance - amount));
    //     }
    // }

    function transferEnergy(int amount, uint transferTo, uint transferFrom) internal {
        if(smartMeterList[transferFrom].available){
            updateSmartMeter(transferTo, smartMeterList[transferTo].energy_generated, smartMeterList[transferTo].energy_sold, (smartMeterList[transferTo].current_balance + amount));
            updateSmartMeter(transferFrom, smartMeterList[transferFrom].energy_generated, (smartMeterList[transferFrom].energy_sold + amount), (smartMeterList[transferTo].current_balance - amount));
        }
    }

    // Iterate through available meteres until amount request is completed
    // function makeTransferRequest(address requestAddress, address[] buyFromAddresses, int amount) internal {
    //     int cur_amount = amount;
    //     for(int i = 0; i < buyFromAddresses.length; i++) {
    //         int difference = cur_amount - (smartMeters[buyFromAddresses[i]].current_balance - smartMeters[buyFromAddresses[i]].threshold);
    //         if (difference <= 0) {
    //             transferEnergy(cur_amount, requestAddress, buyFromAddresses[i]);
    //             // EnergyTransferred(requestAddress, buyFromAddresses[i], amount);
    //             return;
    //         }
    //         else {
    //             int newamount = cur_amount - difference;
    //             transferEnergy((cur_amount - difference), requestAddress, buyFromAddresses[i]);
    //             // EnergyTransferred(requestAddress, buyFromAddresses[i], newamount);
    //             cur_amount = cur_amount - newamount;
    //         }
    //     }
    // }

    function makeTransferRequest(uint meterNum, uint[] meters, int amount) internal {
        int cur_amount = amount;
        for(uint i = 0; i < availableMetersCount; i++) {
            if(smartMeterList[meters[i]].available){
                int difference = (smartMeterList[meters[i]].current_balance - smartMeterList[meters[i]].threshold) - cur_amount;
                if (difference >= 0) {
                    transferEnergy(cur_amount, meterNum, meters[i]);
                    // EnergyTransferred(requestAddress, buyFromAddresses[i], amount);
                    return;
                }
                else {
                    int newamount = cur_amount - difference;
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
    //     int availableMetersCount = 0;
    //     int belowThresholdCount = 0;

    //     for (int i = 0; i < smartMeterList.length; i++){
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

    //     for (int z = 0; z < belowThreshold.length; z++){
    //         int amount = smartMeters[belowThreshold[z]].threshold - smartMeters[belowThreshold[z]].current_balance;
    //         makeTransferRequest(belowThreshold[z], availableMeters, amount);
    //         // SmartMeterSynced(belowThreshold[z]);
    //     }
    //     // Iterate through meters below threshold
    //     // Send buy request to first available meter
    //     // Check if amount in buy request greater than surplus over threshold
    //     // Get maximum possible and go to next available until amount fulfilled
    //     // Go to next in belowThreshold list if success
    // }

    function syncMeters() internal {
        availableMetersCount = 0;
        belowThresholdCount = 0;

        uint[] memory availableMeters = new uint[](smartMeterLength);
        uint[] memory belowThreshold = new uint[](smartMeterLength);

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
            for (uint z = 0; z < belowThresholdCount; z++){
                int amount = smartMeterList[belowThreshold[z]].threshold - smartMeterList[belowThreshold[z]].current_balance;
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

    function getSmartMeter(uint meterNum) view public returns(int a, address b, int c, string d, int e, int f, int g, bool h) {
        SmartMeter memory r = smartMeterList[meterNum];
        
        a = r.id;
        b = r.smart_meter_address;
        c = r.threshold;
        d = r.name;
        e = r.energy_generated;
        f = r.energy_sold;
        g = r.current_balance;
        h = r.available;
    }

    function getMeterAddress(uint meterNum) view public returns(address) {
        return smartMeterList[meterNum].smart_meter_address;
    }

    // function getMeterAddresses(int meterNum) view public returns(address[]) {
    //     address[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].smart_meter_address);
    //     }

    //     return r;
    // }
    
    function getThreshold(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].threshold;
    }

    // function getThresholds(int meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].threshold);
    //     }

    //     return r;
    // }

    function getName(uint meterNum) view public returns(string) {
        return smartMeterList[meterNum].name;
    }

    // function getNames(int meterNum) view public returns(string[]) {
    //     string[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].name);
    //     }

    //     return r;
    // }

    function getEnergyGenerated(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].energy_generated;
    }

    // function getEnergiesGenerated(int meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].energy_generated);
    //     }

    //     return r;
    // }

    function getEnergySold(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].energy_sold;
    }

    // function getEnergiesSold(int meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].energy_sold);
    //     }

    //     return r;
    // }

    function getCurrentBalance(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].current_balance;
    }

    // function getCurrentBalances(int meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].current_balance);
    //     }

    //     return r;
    // }

    function getAvailable(uint meterNum) view public returns(bool) {
        return smartMeterList[meterNum].available;
    }

    // function getAvailables(int meterNum) view public returns(bool[]) {
    //     bool[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].available);
    //     }

    //     return r;
    // }

    // Internal function to check if user exists
    // function checkExists(address sender) view internal returns (bool){
    //     bool exists = false;

    //     for (int i = 0; i < smartMeterList.length; i++){
    //         if(sender == smartMeterList[i]){
    //             exists = true;
    //         }
    //     }        

    //     return exists;
    // }
}