var web3Provider = null;
var MicrogridContract;
const nullAddress = "0x0000000000000000000000000000000000000000";
// var csv = require('../node_modules/csv');

// var obj = csv();

var smartMeterList = [];
var smartMeterListStates = [];

// Objects
function smartMeter(id, smart_meter_address, threshold, name, energy_generated, energy_sold, current_balance, available){
  this.id = id
  this.smart_meter_address = smart_meter_address;
  this.threshold = threshold;
  this.name = name;
  this.energy_generated = energy_generated;
  this.energy_sold = energy_sold;
  this.current_balance = current_balance;
  this.available = available;
}

var testInput = [[333492.4, 333492.4, 333492.4, 200095.4, 200095.4, 200095.4, 466889.4, 489122.2, 415012.8, 563231.6, 82190.19375, 82190.19, 82190.19,	49314.12,	49314.12,	49314.12,	115066.3, 120545.6,	102281.1,	138810.1],
                [312132.8,	312132.8,	312132.8,	187279.7,	187279.7,	187279.7,	436985.9,	457794.7,	388431.9,	527157.6,	64818.73125,	64818.73,	64818.73,	38891.24,	38891.24,	38891.24,	90746.22,	95067.47,	80663.31,	109471.6],
                [274681.9,	274681.9,	274681.9,	164809.2,	164809.2,	164809.2,	384554.7,	402866.8,	341826.4,	463907.3,	53018.04375,	53018.04,	53018.04,	31810.83,	31810.83,	31810.83,	74225.26,	77759.8,	65978.01,	89541.59],
                [137219.2,	137219.2,	137219.2,	82331.52,	82331.52,	82331.52,	192106.9,	201254.8,	170761.7,	231748,	46830.65625,	46830.66,	46830.66,	28098.39,	28098.39,	28098.39,	65562.92,	68684.96,	58278.15,	79091.78],
                [7371.944,	7371.944,	7371.944,	4423.166,	4423.166,	4423.166,	10320.72,	10812.18,	9173.974,	12450.39,	47457.9,	47457.9,	47457.9,	28474.74,	28474.74,	28474.74,	66441.06,	69604.92,	59058.72,	80151.12],
                [0.1284,	0.1284,	0.1284,	0.077,	0.077, 0.077,	0.1797,	0.1883,	0.1598,	0.2168,	60523.70625,	60523.71,	60523.71,	36314.22,	36314.22,	36314.22,	84733.19,	88768.1,	75318.39,	102217.8],
                [0,	0, 0,	0,	0,	0,	0,	0,	0,	0,	71569.575,	71569.58,	71569.58,	42941.75,	42941.75,	42941.75,	100197.4,	104968.7,	89064.36,	120873.1]];
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	73865.925	73865.93	73865.93	44319.56	44319.56	44319.56	103412.3	108336.7	91922.04	124751.3],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	61820.71875	61820.72	61820.72	37092.43	37092.43	37092.43	86549.01	90670.39	76932.45	104408.3],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	53889.80625	53889.81	53889.81	32333.88	32333.88	32333.88	75445.73	79038.38	67062.87	91013.9],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	42546.2625	42546.26	42546.26	25527.76	25527.76	25527.76	59564.77	62401.19	52946.46	71855.91],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	34285.78125	34285.78	34285.78	20571.47	20571.47	20571.47	48000.09	50285.81	42666.75	57904.88],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	20688.4125	20688.41	20688.41	12413.05	12413.05	12413.05	28963.78	30343.01	25745.58	34940.43],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	17265.15	17265.15	17265.15	10359.09	10359.09	10359.09	24171.21	25322.22	21485.52	29158.92],
                // [0,	0, 0, 0,	0,	0,	0	0	0	0	16574.11875	16574.12	16574.12	9944.471	9944.471	9944.471	23203.77	24308.71	20625.57	27991.85],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	16350.8625	16350.86	16350.86	9810.518	9810.518	9810.518	22891.21	23981.27	20347.74	27614.79],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	17360.83125	17360.83	17360.83	10416.5	10416.5	10416.5	24305.16	25462.55	21604.59	29320.52],
                // [0,	0, 0,	0,	0,	0,	0	0	0	0	27035.26875	27035.27	27035.27	16221.16	16221.16	16221.16	37849.38	39651.73	33643.89	45659.57]];

// Threshold Profiles
var winter_avg_thresholds = [46452, 46452, 46452, 27871, 27871, 27871, 65033, 68130, 57807, 78453];
var winter_twothirds_tresholds = [60208, 60208, 60208, 36124, 36124, 36124, 84291, 88305, 74925, 101685];
// TODO - change summer thresholds
var summer_avg_thresholds = [46452, 46452, 46452, 27871, 27871, 27871, 65033, 68130, 57807, 78453];
var summer_twothirds_thresholds = [60208, 60208, 60208, 36124, 36124, 36124, 84291, 88305, 74925, 101685];

// Data Profile Options
var isWinter = true;
var aggressive = false;
var weekProfile = true;
var csv_paths = ["../data_profiles/winter_day.csv", "../data_profiles/winter_week.csv", "../data_profiles/summer_day.csv", "../data_profiles/summer_week.csv"]

// Sum of current balances
var totalSupply = 0;

function init() {
  console.log("init");
  // We init web3 so we have access to the blockchain
  initWeb3();
  // registerMaristMetersOnBC();
}

function initWeb3() {
  console.log("initWeb3");
  if (typeof web3 !== 'undefined' && typeof web3.currentProvider !== 'undefined') {
    web3Provider = web3.currentProvider;
    web3 = new Web3(web3Provider);
  } else {    
    console.error('No web3 provider found. Please install Metamask on your browser.');
    alert('No web3 provider found. Please install Metamask on your browser.');
  }

  // we init The Microgrid contract infos so we can interact with it
  initMicrogridContract();
}

function initMicrogridContract() {
  console.log("initialize contract");
  $.getJSON('microgrid.json', function(data) {
    // Get the necessary contract artifact file and instantiate it with truffle-contract
    MicrogridContract = TruffleContract(data);

    // Set the provider for our contract
    MicrogridContract.setProvider(web3Provider);

    // listen to the events emitted by our smart contract
    getEvents();

    console.log(MicrogridContract);

    // We'll retrieve the Wrestlers addresses set in our contract using Web3.js
    // getFirstWrestlerAddress();
    // getSecondWrestlerAddress();
  });
}

// Get logged events
function getEvents () {
  MicrogridContract.deployed().then(function(instance) {
  var events = instance.allEvents(function(error, log){
    if (!error)
      $("#eventsList").prepend('<li>' + log.event + '</li>'); // Using JQuery, we will add new events to a list in our index.html
  });
  }).catch(function(err) {
    console.log(err.message);
  });
}

// CSV In
function getCSV(){
  var csv_path = "";
  var input = [];
  
  if(isWinter){
    if(weekProfile){
      csv_path = csv_paths[1];   
    }
    else{
      csv_path = csv_paths[0];
    }
  }
  else{
    if(weekProfile){
      csv_path = csv_paths[3];
    }
    else{
      csv_path = csv_paths[2];
    }
  }

  obj.from.path(csv_path).to.array(function (data){
    for (var i = 0; i < data.length; i++){
      record = [];
      
      for(var x = 0; x < 10; x++){
        record.push(data[i][x]);
      }
      for(var y = 9; y < 20; y++){
        record.push(data[i][y]);
      }

      input.push(record);
    }
  });

  return input;
}

// CSV Out
// Write smartMeterListStates to csv

// Run single cycle
function runCycle(generation_updates, consumption_updates) {
  updateMetersBC(generation_updates, consumption_updates);
  // updateMetersBC

  // Combined update and sync on contract - reduction in number of API calls
  // MicrogridContract.deployed().then(function(instance) {
  //   instance.syncMeters();
  // }).catch(function(err) {
  //   console.log(err.message);
  // });

  refreshData();
}

function btnRunSimulation(){
  // runCycles(getCSV());
  runCycles(testInput);
  console.log(smartMeterListStates[smartMeterListStates.length-1]);
}

// Multiple Cycles
function runCycles(input){
  var generationUpdates = [];
  var consumptionUpdates = [];

  // setTimeout(function_x(), timeoutInMilliseconds);

  for (i = 0; i < input.length; i++){
    generationUpdates = [];
    consumptionUpdates = [];

    for (x = 0; x < 10; x++){
      generationUpdates.push(input[i][x]);
    }

    for (y = 10; y < 20; y++){
      consumptionUpdates.push(input[i][y])
    }

    setTimeout(runCycle(generationUpdates, consumptionUpdates), 1000);

    smartMeterListStates.push(smartMeterList);
  }
  // var output = "";

  //Print meter_list every cycle
}

// Initialization - Get meters from Blockchain
function retreiveMetersFromBC(){
  smartMeterList = [];
  
  var grid_size = 9;
  // var grid_size = MicrogridContract.deployed().then(function(instance) {
  //   return instance.smartMeterLength.call();
  // }).catch(function(err) {
  //   console.log(err.message);
  // });

  MicrogridContract.deployed().then(function(instance) {
    console.log(0);
    return instance.getSmartMeter.call(0);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(1);
    return instance.getSmartMeter.call(1);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(2);
    return instance.getSmartMeter.call(2);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(3);
    return instance.getSmartMeter.call(3);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(4);
    return instance.getSmartMeter.call(4);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(5);
    return instance.getSmartMeter.call(5);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(6);
    return instance.getSmartMeter.call(6);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(7);
    return instance.getSmartMeter.call(7);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(8);
    return instance.getSmartMeter.call(8);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    console.log(9);
    return instance.getSmartMeter.call(9);
  }).then(function(result) {
    console.log(result);
    smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  }).catch(function(err) {
    console.log(err.message);
  });

  // for (index = 0; index <= grid_size; index++) {
  //   MicrogridContract.deployed().then(function(instance) {
  //     console.log(index);
  //     return instance.getSmartMeter.call(index);
  //   }).then(function(result) {
  //     console.log(result);
  //     smartMeterList.push(new smartMeter(result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]));
  //   }).catch(function(err) {
  //     console.log(err.message);
  //   });
  // }
}

// Used for subsequent updates after smartmeter details have been retreived intially
function updateMeterListFromBC(){
  // var grid_size = smartMeterList.length;

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(0);
  }).then(function(result) {
    smartMeterList[0]["threshold"] = result[2];
    smartMeterList[0]["energy_generated"] = result[4];
    smartMeterList[0]["energy_sold"] = result[5];
    smartMeterList[0]["current_balance"] = result[6];
    smartMeterList[0]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(1);
  }).then(function(result) {
    smartMeterList[1]["threshold"] = result[2];
    smartMeterList[1]["energy_generated"] = result[4];
    smartMeterList[1]["energy_sold"] = result[5];
    smartMeterList[1]["current_balance"] = result[6];
    smartMeterList[1]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(2);
  }).then(function(result) {
    smartMeterList[2]["threshold"] = result[2];
    smartMeterList[2]["energy_generated"] = result[4];
    smartMeterList[2]["energy_sold"] = result[5];
    smartMeterList[2]["current_balance"] = result[6];
    smartMeterList[2]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(3);
  }).then(function(result) {
    smartMeterList[3]["threshold"] = result[2];
    smartMeterList[3]["energy_generated"] = result[4];
    smartMeterList[3]["energy_sold"] = result[5];
    smartMeterList[3]["current_balance"] = result[6];
    smartMeterList[3]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(4);
  }).then(function(result) {
    smartMeterList[4]["threshold"] = result[2];
    smartMeterList[4]["energy_generated"] = result[4];
    smartMeterList[4]["energy_sold"] = result[5];
    smartMeterList[4]["current_balance"] = result[6];
    smartMeterList[4]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(5);
  }).then(function(result) {
    smartMeterList[5]["threshold"] = result[2];
    smartMeterList[5]["energy_generated"] = result[4];
    smartMeterList[5]["energy_sold"] = result[5];
    smartMeterList[5]["current_balance"] = result[6];
    smartMeterList[5]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(6);
  }).then(function(result) {
    smartMeterList[6]["threshold"] = result[2];
    smartMeterList[6]["energy_generated"] = result[4];
    smartMeterList[6]["energy_sold"] = result[5];
    smartMeterList[6]["current_balance"] = result[6];
    smartMeterList[6]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(7);
  }).then(function(result) {
    smartMeterList[7]["threshold"] = result[2];
    smartMeterList[7]["energy_generated"] = result[4];
    smartMeterList[7]["energy_sold"] = result[5];
    smartMeterList[7]["current_balance"] = result[6];
    smartMeterList[7]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(8);
  }).then(function(result) {
    smartMeterList[8]["threshold"] = result[2];
    smartMeterList[8]["energy_generated"] = result[4];
    smartMeterList[8]["energy_sold"] = result[5];
    smartMeterList[8]["current_balance"] = result[6];
    smartMeterList[8]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });

  MicrogridContract.deployed().then(function(instance) {
    return instance.getSmartMeter.call(9);
  }).then(function(result) {
    smartMeterList[9]["threshold"] = result[2];
    smartMeterList[9]["energy_generated"] = result[4];
    smartMeterList[9]["energy_sold"] = result[5];
    smartMeterList[9]["current_balance"] = result[6];
    smartMeterList[9]["available"] = result[7];
    console.log(result);
  }).catch(function(err) {
    console.log(err.message);
  });
}

// function pushMeterListToBC(){
//   for (i = 0; i < meterList.length; i++){
//     updateMeterBC(i, meterList[i].energy_generated, meterList[i].energy_sold, meterList[i].current_balance);
//   }
// }
function initMeterValues(){
  retreiveMetersFromBC();
  console.log(smartMeterList);
  
  // for(i = 0; i < smartMeterList.length; i++){
  //   updateMeterBC(i, 0, 0, 100000);
  //   console.log("round " + i);
  // }

  // refreshData();
  // updateMeterListFromBC();
}

function refreshData(){
  updateMeterListFromBC();

  console.log(smartMeterList);

  refreshTableData();
  refreshChartData();
  updateTotalSupply();
}

// Update Meter object on blockchain
function updateMeterBC(meter_num, energy_generated, current_balance){
  MicrogridContract.deployed().then(function(instance) {
    instance.updateSmartMeter(meter_num, energy_generated, current_balance);
  }).catch(function(err) {
    console.log(err.message);
  });
}

// Update with array
function updateMetersBC(generation_updates, consumption_updates){
  MicrogridContract.deployed().then(function(instance) {
    instance.inputAndSync(generation_updates, consumption_updates);
  }).catch(function(err) {
    console.log(err.message);
  });
}

// function updateMeterInMeterList(meter_num, energy_generated, energy_sold, current_balance){
//   meterList[i].energy_generated = energy_generated;
//   meterList[i].energy_sold = energy_sold;
//   meterList[i].current_balance = current_balance;
// }

// Intial address/smartmeter regsitration on Blockchain
function registerMaristMetersOnBC(){
  var names = ["Gartland 1", "Gartland 2", "Fontaine", "Foy 1", "Foy 2", "Foy 3", "Dyson", "Lowell-Thomas", "Hancock", "Library"];
  for(i = 0; i < names.length; i++){
    MicrogridContract.deployed().then(function(instance) {
      instance.registerSmartMeter(i, web3.eth.accounts[i], winter_twothirds_tresholds[i], names[i]);
    }).catch(function(err) {
      console.log(err.message);
    });
  }  
}

// Run on threshold profile selected - update thresholds on Blockchain
function updateThresholdsProfile(){
  if(isWinter){
    if(aggressive){
      setSmartMeterThresholds(winter_twothirds_tresholds);
    }
    else{
      setSmartMeterThresholds(winter_avg_thresholds);
    }
  }
  else{
    if(aggressive){
      setSmartMeterThresholds(summer_twothirds_thresholds);
    }
    else{
      setSmartMeterThresholds(summer_avg_thresholds);
    }
  }
}

// Update smart meter thresholds on Blockchain
function setSmartMeterThresholds(thresholds){
  MicrogridContract.deployed().then(function(instance) {
    instance.setSmartMeterThresholdArray(thresholds);
  }).then(function(result) {
    // refreshData();
  }).catch(function(err) {
    console.log(err.message);
  });
}

// Update total supply variable and page total supply
function updateTotalSupply(){
  totalSupply = 0;
  
  for(i = 0; i < smartMeterList.length; i++){
    totalSupply = new Number(smartMeterList[i].current_balance) + totalSupply;
    console.log(totalSupply);
  }

  document.getElementById('totalSupplyNumber').innerHTML = totalSupply + " Wh";
}

// Set variable options for data profile - summer vs winter, 24hrs vs 7 days
function setDataProfile(isItWinter, isWeek){
  if(isItWinter){
    isWinter = true;
  }
  else{
    isWinter = false;
  }

  if(isWeek){
    weekProfile = true;
  }
  else{
    weekProfile = false;
  }
}

// Set threshold profile - run on threshold profile selection
function setThresholdProfile(beAggressive){
  if(beAggressive){
    aggressive = true;
  }
  else{
    aggressive = false;
  }

  updateThresholdsProfile();
}

// Chart functions
function removeChartData(chart) {
  chart.data.labels.pop();
  chart.data.datasets.forEach((dataset) => {
    dataset.data.pop();
  });
  chart.update();
}

function addChartData(chart, label, data) {
  chart.data.labels.push(label);
  chart.data.datasets.forEach((dataset) => {
    dataset.data.push(data);
  });
  chart.update();
}

function refreshChartData(){
  for(i = 0; i < smartMeterList.length; i++){
    removeChartData(myChart);
  }
  for(x = 0; x < smartMeterList.length; x++){
    addChartData(myChart, smartMeterList[x].name, smartMeterList[x].current_balance);
  }
}

// Temporary - TODO: switch to dynamic table
function refreshTableData(){
  document.getElementById("g1_address").innerHTML = smartMeterList[0].smart_meter_address;
  document.getElementById("g1_threshold").innerHTML = smartMeterList[0].threshold;
  document.getElementById("g1_generated").innerHTML = smartMeterList[0].energy_generated;
  document.getElementById("g1_sold").innerHTML = smartMeterList[0].energy_sold;
  document.getElementById("g1_balance").innerHTML = smartMeterList[0].current_balance;
  document.getElementById("g1_available").innerHTML = smartMeterList[0].available;

  document.getElementById("g2_address").innerHTML = smartMeterList[1].smart_meter_address;
  document.getElementById("g2_threshold").innerHTML = smartMeterList[1].threshold;
  document.getElementById("g2_generated").innerHTML = smartMeterList[1].energy_generated;
  document.getElementById("g2_sold").innerHTML = smartMeterList[1].energy_sold;
  document.getElementById("g2_balance").innerHTML = smartMeterList[1].current_balance;
  document.getElementById("g2_available").innerHTML = smartMeterList[1].available;

  document.getElementById("fo_address").innerHTML = smartMeterList[2].smart_meter_address;
  document.getElementById("fo_threshold").innerHTML = smartMeterList[2].threshold;
  document.getElementById("fo_generated").innerHTML = smartMeterList[2].energy_generated;
  document.getElementById("fo_sold").innerHTML = smartMeterList[2].energy_sold;
  document.getElementById("fo_balance").innerHTML = smartMeterList[2].current_balance;
  document.getElementById("fo_available").innerHTML = smartMeterList[2].available;

  document.getElementById("f1_address").innerHTML = smartMeterList[3].smart_meter_address;
  document.getElementById("f1_threshold").innerHTML = smartMeterList[3].threshold;
  document.getElementById("f1_generated").innerHTML = smartMeterList[3].energy_generated;
  document.getElementById("f1_sold").innerHTML = smartMeterList[3].energy_sold;
  document.getElementById("f1_balance").innerHTML = smartMeterList[3].current_balance;
  document.getElementById("f1_available").innerHTML = smartMeterList[3].available;

  document.getElementById("f2_address").innerHTML = smartMeterList[4].smart_meter_address;
  document.getElementById("f2_threshold").innerHTML = smartMeterList[4].threshold;
  document.getElementById("f2_generated").innerHTML = smartMeterList[4].energy_generated;
  document.getElementById("f2_sold").innerHTML = smartMeterList[4].energy_sold;
  document.getElementById("f2_balance").innerHTML = smartMeterList[4].current_balance;
  document.getElementById("f2_available").innerHTML = smartMeterList[4].available;

  document.getElementById("f3_address").innerHTML = smartMeterList[5].smart_meter_address;
  document.getElementById("f3_threshold").innerHTML = smartMeterList[5].threshold;
  document.getElementById("f3_generated").innerHTML = smartMeterList[5].energy_generated;
  document.getElementById("f3_sold").innerHTML = smartMeterList[5].energy_sold;
  document.getElementById("f3_balance").innerHTML = smartMeterList[5].current_balance;
  document.getElementById("f3_available").innerHTML = smartMeterList[5].available;

  document.getElementById("dy_address").innerHTML = smartMeterList[6].smart_meter_address;
  document.getElementById("dy_threshold").innerHTML = smartMeterList[6].threshold;
  document.getElementById("dy_generated").innerHTML = smartMeterList[6].energy_generated;
  document.getElementById("dy_sold").innerHTML = smartMeterList[6].energy_sold;
  document.getElementById("dy_balance").innerHTML = smartMeterList[6].current_balance;
  document.getElementById("dy_available").innerHTML = smartMeterList[6].available;

  document.getElementById("lt_address").innerHTML = smartMeterList[7].smart_meter_address;
  document.getElementById("lt_threshold").innerHTML = smartMeterList[7].threshold;
  document.getElementById("lt_generated").innerHTML = smartMeterList[7].energy_generated;
  document.getElementById("lt_sold").innerHTML = smartMeterList[7].energy_sold;
  document.getElementById("lt_balance").innerHTML = smartMeterList[7].current_balance;
  document.getElementById("lt_available").innerHTML = smartMeterList[7].available;

  document.getElementById("ha_address").innerHTML = smartMeterList[8].smart_meter_address;
  document.getElementById("ha_threshold").innerHTML = smartMeterList[8].threshold;
  document.getElementById("ha_generated").innerHTML = smartMeterList[8].energy_generated;
  document.getElementById("ha_sold").innerHTML = smartMeterList[8].energy_sold;
  document.getElementById("ha_balance").innerHTML = smartMeterList[8].current_balance;
  document.getElementById("ha_available").innerHTML = smartMeterList[8].available;

  document.getElementById("li_address").innerHTML = smartMeterList[9].smart_meter_address;
  document.getElementById("li_threshold").innerHTML = smartMeterList[9].threshold;
  document.getElementById("li_generated").innerHTML = smartMeterList[9].energy_generated;
  document.getElementById("li_sold").innerHTML = smartMeterList[9].energy_sold;
  document.getElementById("li_balance").innerHTML = smartMeterList[9].current_balance;
  document.getElementById("li_available").innerHTML = smartMeterList[9].available;
}

// function getFirstWrestlerAddress() {
//   WrestlingContract.deployed().then(function(instance) {
//     return instance.wrestler1.call();
//   }).then(function(result) {
//     $("#wrestler1").text(result); // Using JQuery again, we will modify the html tag with id wrestler1 with the returned text from our call on the instance of the wrestling contract we deployed
//   }).catch(function(err) {
//     console.log(err.message);
//   });
// }

// function getSecondWrestlerAddress() {
//   WrestlingContract.deployed().then(function(instance) {
//     return instance.wrestler2.call();
//   }).then(function(result) {
//     if(result != nullAddress) {
//       $("#wrestler2").text(result);
//       $("#registerToFight").remove(); // By clicking on the button with the ID registerToFight, a user can register as second wrestler, so we need to remove the button if a second wrestler is set 
//     } else {
//       $("#wrestler2").text("Undecided, you can register to wrestle in this event!");
//     }   
//   }).catch(function(err) {
//     console.log(err.message);
//   });
// }

// function registerAsSecondWrestler () {
//   web3.eth.getAccounts(function(error, accounts) {
//   if (error) {
//     console.log(error);
//   } else {
//     if(accounts.length <= 0) {
//       alert("No account is unlocked, please authorize an account on Metamask.")
//     } else {
//       WrestlingContract.deployed().then(function(instance) {
//         return instance.registerAsAnOpponent({from: accounts[0]});
//       }).then(function(result) {
//         console.log('Registered as an opponent')
//         getSecondWrestlerAddress();
//       }).catch(function(err) {
//         console.log(err.message);
//       });
//     }
//   }
//   });
// }

// When the page loads, this will call the init() function
$(function() {
  $(window).load(function() {
    init();
  });
});