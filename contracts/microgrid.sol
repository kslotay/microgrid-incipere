pragma solidity 0.4.21;

contract microgrid {

    // Constructor
    function microgrid() public {

    }

    // Smart meter struct
    struct SmartMeter {
        int id;
        address smart_meter_address;
        int threshold;
        string name;
        int energy_generated;
        int energy_sold;
        int energy_cap;
        int current_balance;
        bool available;
        // bool frozen;
    }

    // Logging events
    event SmartMeterRegistered(address smart_meter_address);
    event SmartMeterUpdated(address smart_meter_address);
    event SmartMeterThresholdUpdated(address smart_meter_address);
    event SmartMeterCapUpdated(address smart_meter_address);
    event SmartMeterSynced(address smart_meter_address);
    event EnergyTransferred(uint transferTo, uint transferFrom, int amount);

    // Mapping/dictionary of kv pairs for SmartMeters
    mapping(uint => SmartMeter) public smartMeterList;
    // Keep track of smartMeterList length
    uint public smartMeterLength = 0;

    // Track count of meters above and below threshold limit
    uint public availableMetersCount = 0;
    uint public belowThresholdCount = 0;


    // Register smart meter
    // TODO: implement meter energy caps
    function registerSmartMeter(uint meterNum, address meterAddress, int threshold, string name) public {
        smartMeterList[meterNum].smart_meter_address = meterAddress;
        smartMeterList[meterNum].threshold = threshold;
        smartMeterList[meterNum].name = name;
        smartMeterList[meterNum].energy_generated = 0;
        smartMeterList[meterNum].energy_sold = 0;
        // smartMeterList[meterNum].energy_cap = energy_cap;
        smartMeterList[meterNum].current_balance = 100000;
        smartMeterList[meterNum].available = false;
        // smartMeterList[meterNum].frozen = false;
        smartMeterLength++;

        emit SmartMeterRegistered(meterAddress);
        // emit SmartMeterRegistered(msg.sender);
    }


    // Meter update functions
    // Update available meters
    function updateAvailable(uint meterNum) internal {
        if(smartMeterList[meterNum].threshold < smartMeterList[meterNum].current_balance) {
            smartMeterList[meterNum].available = true;
        }
        else {
            smartMeterList[meterNum].available = false;
        }
    }

    // Internal update function, used by sync
    function updateSmartMeter(uint meterNum, int energy_generated, int energy_sold, int current_balance) internal {
        smartMeterList[meterNum].energy_generated = energy_generated;
        smartMeterList[meterNum].energy_sold = energy_sold;
        smartMeterList[meterNum].current_balance = current_balance;
        updateAvailable(meterNum);

        emit SmartMeterUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    // External update meter function
    function updateSmartMeterExt(uint meterNum, int energy_generated, int energy_consumed) public {
        int new_balance = smartMeterList[meterNum].current_balance + energy_generated - energy_consumed;
        
        smartMeterList[meterNum].energy_generated = smartMeterList[meterNum].energy_generated + energy_generated;
        
        if(new_balance < smartMeterList[meterNum].energy_cap) {
            smartMeterList[meterNum].current_balance = new_balance;
        }
        else{
            smartMeterList[meterNum].current_balance = smartMeterList[meterNum].energy_cap;
        }

        updateAvailable(meterNum);

        emit SmartMeterUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    // Batch update meter function, calls sync after updates
    function updateSmartMeters(int[] generation_updates, int[] consumption_updates) public {
        for (uint i = 0; i < smartMeterLength; i++){
            updateSmartMeterExt(i, generation_updates[i], consumption_updates[i]);
        }

        syncMeters();
    }

    // Update meter threshold
    function setSmartMeterThreshold(uint meterNum, int threshold) public {
        // require(smartMeterList[meterNum].smart_meter_address == msg.sender);
        smartMeterList[meterNum].threshold = threshold;

        emit SmartMeterThresholdUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    // Batch update all thresholds from array
    function setSmartMeterThresholdArray(int[] thresholds) public {
        for (uint i = 0; i < smartMeterLength; i++){
            setSmartMeterThreshold(i, thresholds[i]);
        }
    }

    // Update meter cap
    function setSmartMeterCap(uint meterNum, int cap) public {
        // require(smartMeterList[meterNum].smart_meter_address == msg.sender);
        smartMeterList[meterNum].energy_cap = cap;

        emit SmartMeterCapUpdated(smartMeterList[meterNum].smart_meter_address);
    }

    function setSmartMeterCapArray(int[] caps) public {
        for (uint i = 0; i < smartMeterLength; i++){
            setSmartMeterCap(i, caps[i]);
        }
    }


    // Meter sync functions
    // Transfer energy from one meter to another, internal function, called by transfer request function
    function transferEnergy(int amount, uint transferTo, uint transferFrom) internal {
        if(smartMeterList[transferFrom].available){
            updateSmartMeter(transferTo, smartMeterList[transferTo].energy_generated, smartMeterList[transferTo].energy_sold, (smartMeterList[transferTo].current_balance + amount));
            updateSmartMeter(transferFrom, smartMeterList[transferFrom].energy_generated, (smartMeterList[transferFrom].energy_sold + amount), (smartMeterList[transferTo].current_balance - amount));
            emit EnergyTransferred(transferFrom, transferTo, amount);
        }
    }

    // Internal transfer request function given an array of available meters, internal function called by sync
    function makeTransferRequest(uint meterNum, uint[] meters, int amount) internal {
        int cur_amount = amount;

        for(uint i = 0; i < availableMetersCount; i++) {
            if(smartMeterList[meters[i]].available){
                int difference = (smartMeterList[meters[i]].current_balance - smartMeterList[meters[i]].threshold) - cur_amount;
                
                if (difference >= 0) {
                    transferEnergy(cur_amount, meterNum, meters[i]);
                    return;
                }
                else {
                    int newamount = cur_amount - (smartMeterList[meters[i]].current_balance - smartMeterList[meters[i]].threshold);
                    
                    transferEnergy((newamount), meterNum, meters[i]);
                    cur_amount = cur_amount - newamount;
                }
            }
        }

        // If not enough is available it falls through
    }

    // Internal meter synchronization function
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
                emit SmartMeterSynced(smartMeterList[belowThreshold[z]].smart_meter_address);
            }
        }
        
        // Iterate through meters below threshold
        // Send buy request to first available meter
        // Check if amount in buy request greater than surplus over threshold
        // Get maximum possible and go to next available until amount fulfilled
        // Go to next in belowThreshold list if success
    }

    // Getter functions

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

    function getThreshold(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].threshold;
    }

    function getName(uint meterNum) view public returns(string) {
        return smartMeterList[meterNum].name;
    }

    function getEnergyGenerated(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].energy_generated;
    }

    function getEnergySold(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].energy_sold;
    }

    function getEnergyCap(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].energy_cap;
    }

    function getCurrentBalance(uint meterNum) view public returns(int) {
        return smartMeterList[meterNum].current_balance;
    }

    function getAvailable(uint meterNum) view public returns(bool) {
        return smartMeterList[meterNum].available;
    }

    // Plural getter functions (unused for now)

    // function getMeterAddresses(uint[] meterNum) view public returns(address[]) {
    //     address[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].smart_meter_address);
    //     }

    //     return r;
    // }

    // function getThresholds(uint[] meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].threshold);
    //     }

    //     return r;
    // }

    // function getNames(uint[] meterNum) view public returns(string[]) {
    //     string[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].name);
    //     }

    //     return r;
    // }

    // function getEnergiesGenerated(uint[] meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].energy_generated);
    //     }

    //     return r;
    // }

    // function getEnergiesSold(uint[] meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].energy_sold);
    //     }

    //     return r;
    // }

    // function getEnergyCaps(uint[] meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].energy_cap);
    //     }

    //     return r;
    // }

    // function getCurrentBalances(int meterNum) view public returns(int[]) {
    //     int[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].current_balance);
    //     }

    //     return r;
    // }

    // function getAvailables(int meterNum) view public returns(bool[]) {
    //     bool[] memory r;

    //     for(int i = 0; i < smartMeterLength; i++) {
    //         r.push(smartMeterList[meterNum].available);
    //     }

    //     return r;
    // }
}