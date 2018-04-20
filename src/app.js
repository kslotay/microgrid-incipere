var web3Provider = null;
var MicrogridContract;
const nullAddress = "0x0000000000000000000000000000000000000000";
// var csv = require('../node_modules/csv');
// var csvWriter = require('../node_modules/csv-write-stream');

// var obj = csv();
// var writer = csvWriter({headers: ["id", "smart_meter_address", "threshold", "name", "energy_generated", "energy_sold", "current_balance", "available"]});

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

var dayWinter = [[172819.4437,172819.4437,172819.4437,103691.6663,103691.6663,103691.6663,241947.2211,253468.5174,215064.1966,291872.8382,99774.28125,99774.28125,99774.28125,59864.56875,59864.56875,59864.56875,139683.9938,146335.6125,124163.55,168507.675],
  [112570.215,112570.215,112570.215,67542.129,67542.129,67542.129,157598.301,165102.982,140087.3787,190118.5854,109384.9313,109384.9313,109384.9313,65630.95875,65630.95875,65630.95875,153138.9038,160431.2325,136123.47,184738.995],
  [150175.6805,150175.6805,150175.6805,90105.4083,90105.4083,90105.4083,210245.9527,220257.6648,186885.2913,253630.0382,98530.425,98530.425,98530.425,59118.255,59118.255,59118.255,137942.595,144511.29,122615.64,166406.94],
  [21242.7933,21242.7933,21242.7933,12745.676,12745.676,12745.676,29739.9107,31156.0969,26435.4761,35876.7176,92119.78125,92119.78125,92119.78125,55271.86875,55271.86875,55271.86875,128967.6938,135109.0125,114637.95,155580.075],
  [0.0625,0.0625,0.0625,0.0375,0.0375,0.0375,0.0874,0.0916,0.0777,0.1055,139535.1563,139535.1563,139535.1563,83721.09375,83721.09375,83721.09375,195349.2188,204651.5625,173643.75,235659.375],
  [0,0,0,0,0,0,0,0,0,0,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [0,0,0,0,0,0,0,0,0,0,98498.53125,98498.53125,98498.53125,59099.11875,59099.11875,59099.11875,137897.9438,144464.5125,122575.95,166353.075],
  [0,0,0,0,0,0,0,0,0,0,56292.46875,56292.46875,56292.46875,33775.48125,33775.48125,33775.48125,78809.45625,82562.2875,70052.85,95071.725],
  [0,0,0,0,0,0,0,0,0,0,36752.23125,36752.23125,36752.23125,22051.33875,22051.33875,22051.33875,51453.12375,53903.2725,45736.11,62070.435],
  [0,0,0,0,0,0,0,0,0,0,32967.50625,32967.50625,32967.50625,19780.50375,19780.50375,19780.50375,46154.50875,48352.3425,41026.23,55678.455],
  [0.0507,0.0507,0.0507,0.0304,0.0304,0.0304,0.071,0.0744,0.0631,0.0857,46894.44375,46894.44375,46894.44375,28136.66625,28136.66625,28136.66625,65652.22125,68778.5175,58357.53,79199.505],
  [62206.1518,62206.1518,62206.1518,37323.6911,37323.6911,37323.6911,87088.6125,91235.6893,77412.1,105059.2786,99859.33125,99859.33125,99859.33125,59915.59875,59915.59875,59915.59875,139803.0638,146460.3525,124269.39,168651.315],
  [115168.0451,115168.0451,115168.0451,69100.8271,69100.8271,69100.8271,161235.2632,168913.1328,143320.2339,194506.0318,61799.45625,61799.45625,61799.45625,37079.67375,37079.67375,37079.67375,86519.23875,90639.2025,76905.99,104372.415]];

var fiveDayWinter = [[447536.52,447536.52,447536.52,268521.9121,268521.9121,268521.9121,626551.1279,656386.8959,556934.336,755839.456,310060.4063,310060.4063,310060.4063,186036.2438,186036.2438,186036.2438,434084.5688,454755.2625,385852.95,523657.575],
  [0,0,0,0,0,0,0,0,0,0,348258.4875,348258.4875,348258.4875,208955.0925,208955.0925,208955.0925,487561.8826,510779.115,433388.34,588169.89],
  [0,0,0,0,0,0,0,0,0,0,115274.6438,115274.6438,115274.6438,69164.78625,69164.78625,69164.78625,161384.5013,169069.4775,143452.89,194686.065],
  [555707.6961,555707.6961,555707.6961,333424.6175,333424.6175,333424.6175,777990.7745,815037.9542,691547.3551,938528.5534,313313.5688,313313.5688,313313.5688,187988.1413,187988.1413,187988.1413,438638.9963,459526.5675,389901.33,529151.805],
  [240808.6791,240808.6791,240808.6791,144485.2075,144485.2075,144485.2075,337132.1507,353186.0627,299673.0228,406699.1024,310060.4063,310060.4063,310060.4063,186036.2438,186036.2438,186036.2438,434084.5688,454755.2625,385852.95,523657.575],
  [0,0,0,0,0,0,0,0,0,0,348258.4875,348258.4875,348258.4875,208955.0925,208955.0925,208955.0925,487561.8826,510779.115,433388.34,588169.89],
  [0,0,0,0,0,0,0,0,0,0,115274.6438,115274.6438,115274.6438,69164.78625,69164.78625,69164.78625,161384.5013,169069.4775,143452.89,194686.065],
  [460278.4038,460278.4038,460278.4038,276167.0422,276167.0422,276167.0422,644389.7652,675074.9922,572790.9025,777359.0817,313313.5688,313313.5688,313313.5688,187988.1413,187988.1413,187988.1413,438638.9963,459526.5675,389901.33,529151.805],
  [636079.4611,636079.4611,636079.4611,381647.6767,381647.6767,381647.6767,890511.2454,932916.5429,791565.5516,1074267.534,310060.4063,310060.4063,310060.4063,186036.2438,186036.2438,186036.2438,434084.5688,454755.2625,385852.95,523657.575],
  [0,0,0,0,0,0,0,0,0,0,348258.4875,348258.4875,348258.4875,208955.0925,208955.0925,208955.0925,487561.8826,510779.115,433388.34,588169.89],
  [0,0,0,0,0,0,0,0,0,0,115274.6438,115274.6438,115274.6438,69164.78625,69164.78625,69164.78625,161384.5013,169069.4775,143452.89,194686.065],
  [907665.8182,907665.8182,907665.8182,544599.4909,544599.4909,544599.4909,1270732.146,1331243.2,1129539.685,1532946.715,313313.5688,313313.5688,313313.5688,187988.1413,187988.1413,187988.1413,438638.9963,459526.5675,389901.33,529151.805],
  [937300.8801,937300.8801,937300.8801,562380.5281,562380.5281,562380.5281,1312221.232,1374707.958,1166418.873,1582997.042,310060.4063,310060.4063,310060.4063,186036.2438,186036.2438,186036.2438,434084.5688,454755.2625,385852.95,523657.575],
  [0,0,0,0,0,0,0,0,0,0.0001,348258.4875,348258.4875,348258.4875,208955.0925,208955.0925,208955.0925,487561.8826,510779.115,433388.34,588169.89],
  [0,0,0,0,0,0,0,0,0,0,115274.6438,115274.6438,115274.6438,69164.78625,69164.78625,69164.78625,161384.5013,169069.4775,143452.89,194686.065],
  [355391.882,355391.882,355391.882,213235.1292,213235.1292,213235.1292,497548.6348,521241.4269,442265.4532,600217.4008,313313.5688,313313.5688,313313.5688,187988.1413,187988.1413,187988.1413,438638.9963,459526.5675,389901.33,529151.805],
  [189919.1612,189919.1612,189919.1612,113951.4968,113951.4968,113951.4968,265886.8257,278548.1032,236343.845,320752.3612,310060.4063,310060.4063,310060.4063,186036.2438,186036.2438,186036.2438,434084.5688,454755.2625,385852.95,523657.575],
  [0,0,0,0,0,0,0,0,0,0,348258.4875,348258.4875,348258.4875,208955.0925,208955.0925,208955.0925,487561.8826,510779.115,433388.34,588169.89],
  [0,0,0,0,0,0,0,0,0,0,114572.9813,114572.9813,114572.9813,68743.78875,68743.78875,68743.78875,160402.1738,168040.3725,142579.71,193501.035],
  [430149.8271,430149.8271,430149.8271,258089.8963,258089.8963,258089.8963,602209.758,630886.4131,535297.5626,726475.2636,393898.4438,393898.4438,393898.4438,236339.0663,236339.0663,236339.0663,551457.8213,577717.7175,490184.73,665250.705]];

var daySummer = [[867349.8419,867349.8419,867349.8419,520409.9052,520409.9052,520409.9052,1214289.779,1272113.102,1079368.692,1464857.511,149921.8875,149921.8875,149921.8875,89953.1325,89953.1325,89953.1325,209890.6425,219885.435,186569.46,253201.41],
  [742891.7038,742891.7038,742891.7038,445735.0222,445735.0222,445735.0222,1040048.385,1089574.499,924487.4536,1254661.544,106046.7188,106046.7188,106046.7188,63628.03125,63628.03125,63628.03125,148465.4063,155535.1875,131969.25,179101.125],
  [552730.8362,552730.8362,552730.8362,331638.5018,331638.5018,331638.5018,773823.1707,810671.8931,687842.8184,933500.9677,94862.64375,94862.64375,94862.64375,56917.58625,56917.58625,56917.58625,132807.7013,139131.8775,118051.29,160212.465],
  [388787.1425,388787.1425,388787.1425,233272.2855,233272.2855,233272.2855,544301.9996,570221.1424,483823.9996,656618.2852,127064.7,127064.7,127064.7,76238.82,76238.82,76238.82,177890.58,186361.56,158124.96,214598.16],
  [7420.0623,7420.0623,7420.0623,4452.0374,4452.0374,4452.0374,10388.0872,10882.7581,9233.8553,12531.6608,128202.2438,128202.2438,128202.2438,76921.34625,76921.34625,76921.34625,179483.1413,188029.9575,159540.57,216519.345],
  [0.0005,0.0005,0.0005,0.0003,0.0003,0.0003,0.0007,0.0008,0.0006,0.0009,95394.20625,95394.20625,95394.20625,57236.52375,57236.52375,57236.52375,133551.8888,139911.5025,118712.79,161110.215],
  [0,0,0,0,0,0,0,0,0,0,48063.88125,48063.88125,48063.88125,28838.32875,28838.32875,28838.32875,67289.43375,70493.6925,59812.83,81174.555],
  [0.0005,0.0005,0.0005,0.0003,0.0003,0.0003,0.0007,0.0007,0.0006,0.0008,40409.38125,40409.38125,40409.38125,24245.62875,24245.62875,24245.62875,56573.13375,59267.0925,50287.23,68246.955],
  [8416.5296,8416.5296,8416.5296,5049.9178,5049.9178,5049.9178,11783.1415,12344.2434,10473.9036,14214.5833,54038.64375,54038.64375,54038.64375,32423.18625,32423.18625,32423.18625,75654.10125,79256.6775,67248.09,91265.265],
  [418964.2792,418964.2792,418964.2792,251378.5675,251378.5675,251378.5675,586549.9908,614480.9428,521377.7697,707584.116,108204.8625,108204.8625,108204.8625,64922.9175,64922.9175,64922.9175,151486.8075,158700.465,134654.94,182745.99],
  [454865.3173,454865.3173,454865.3173,272919.1904,272919.1904,272919.1904,636811.4443,667135.7987,566054.6171,768216.9804,121685.2875,121685.2875,121685.2875,73011.1725,73011.1725,73011.1725,170359.4025,178471.755,151430.58,205512.93],
  [473857.188,473857.188,473857.188,284314.3129,284314.3129,284314.3129,663400.0633,694990.5425,589688.9451,800292.1398,122546.4188,122546.4188,122546.4188,73527.85125,73527.85125,73527.85125,171564.9863,179734.7475,152502.21,206967.285],
  [257948.2269,257948.2269,257948.2269,154768.9362,154768.9362,154768.9362,361127.5177,378324.0661,321002.2379,435645.8943,67997.475,67997.475,67997.475,40798.485,40798.485,40798.485,95196.465,99729.63,84619.08,114840.18]];

var fiveDaySummer = 
 [[2162972.382,2162972.382,2162972.382,1297783.429,1297783.429,3028161.335,1297783.429,3172359.494,2691698.964,3653020.023,350831.25,350831.25,350831.25,210498.75,210498.75,210498.75,491163.75,514552.5,436590,592515],
  [396207.2053,396207.2053,396207.2053,237724.3232,237724.3232,554690.0875,237724.3232,581103.9013,493057.8555,669149.9469,350661.15,350661.15,350661.15,210396.69,210396.69,210396.69,490925.61,514303.02,436378.32,592227.72],
  [8416.5301,8416.5301,8416.5301,5049.9181,5049.9181,11783.1422,5049.9181,12344.2441,10473.9042,14214.5841,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [1347686.785,1347686.785,1347686.785,808612.0708,808612.0708,1886761.498,808612.0708,1976607.284,1677121.332,2276093.236,352436.5688,352436.5688,352436.5688,211461.9413,211461.9413,211461.9413,493411.1963,516906.9675,438587.73,595226.205],
  [994973.471,994973.471,994973.471,596984.0825,596984.0825,1392962.859,596984.0825,1459294.424,1238189.208,1680399.639,335649.825,335649.825,335649.825,201389.895,201389.895,201389.895,469909.755,492286.41,417697.56,566875.26],
  [279978.7932,279978.7932,279978.7932,167987.2757,167987.2757,391970.3104,167987.2757,410635.5632,348418.0537,472853.0728,366044.5688,366044.5688,366044.5688,219626.7413,219626.7413,219626.7413,512462.3963,536865.3675,455522.13,618208.605],
  [2671.3843,2671.3843,2671.3843,1602.8306,1602.8306,3739.938,1602.8306,3918.0303,3324.3893,4511.6712,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [1078102.269,1078102.269,1078102.269,646861.3615,646861.3615,1509343.177,646861.3615,1581216.661,1341638.379,1820794.943,352436.5688,352436.5688,352436.5688,211461.9413,211461.9413,211461.9413,493411.1963,516906.9675,438587.73,595226.205],
  [361772.9677,361772.9677,361772.9677,217063.7807,217063.7807,506482.1547,217063.7807,530600.3525,450206.3597,610994.3453,335649.825,335649.825,335649.825,201389.895,201389.895,201389.895,469909.755,492286.41,417697.56,566875.26],
  [128972.3223,128972.3223,128972.3223,77383.3934,77383.3934,180561.2513,77383.3934,189159.4062,160498.89,217819.9223,366044.5688,366044.5688,366044.5688,219626.7413,219626.7413,219626.7413,512462.3963,536865.3675,455522.13,618208.605],
  [3393.5347,3393.5347,3393.5347,2036.1208,2036.1208,4750.9485,2036.1208,4977.1841,4223.0654,5731.303,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [931089.6913,931089.6913,931089.6913,558653.8148,558653.8148,1303525.568,558653.8148,1365598.214,1158689.394,1572507.034,352436.5688,352436.5688,352436.5688,211461.9413,211461.9413,211461.9413,493411.1963,516906.9675,438587.73,595226.205],
  [1653616.313,1653616.313,1653616.313,992169.7876,992169.7876,2315062.838,992169.7876,2425303.925,2057833.634,2792774.217,335649.825,335649.825,335649.825,201389.895,201389.895,201389.895,469909.755,492286.41,417697.56,566875.26],
  [406520.4058,406520.4058,406520.4058,243912.2435,243912.2435,569128.5681,243912.2435,596229.9284,505892.0605,686567.7963,366044.5688,366044.5688,366044.5688,219626.7413,219626.7413,219626.7413,512462.3963,536865.3675,455522.13,618208.605],
  [3352.2523,3352.2523,3352.2523,2011.3514,2011.3514,4693.1533,2011.3514,4916.6369,4171.6918,5661.5817,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [1660395.52,1660395.52,1660395.52,996237.312,996237.312,2324553.728,996237.312,2435246.762,2066269.98,2804223.545,352436.5688,352436.5688,352436.5688,211461.9413,211461.9413,211461.9413,493411.1963,516906.9675,438587.73,595226.205],
  [2065772.594,2065772.594,2065772.594,1239463.557,1239463.557,2892081.632,1239463.557,3029799.805,2570739.228,3488860.381,335649.825,335649.825,335649.825,201389.895,201389.895,201389.895,469909.755,492286.41,417697.56,566875.26],
  [438705.7836,438705.7836,438705.7836,263223.4702,263223.4702,614188.0971,263223.4702,643435.1493,545944.9752,740925.3234,366044.5688,366044.5688,366044.5688,219626.7413,219626.7413,219626.7413,512462.3963,536865.3675,455522.13,618208.605],
  [2899.1449,2899.1449,2899.1449,1739.4868,1739.4868,4058.8028,1739.4868,4252.0791,3607.8247,4896.3335,142511.9063,142511.9063,142511.9063,85507.14375,85507.14375,85507.14375,199516.6688,209017.4625,177348.15,240686.775],
  [876123.2826,876123.2826,876123.2826,525673.9696,525673.9696,1226572.596,525673.9696,1284980.815,1090286.752,1479674.878,420434.0438,420434.0438,420434.0438,252260.4263,252260.4263,252260.4263,588607.6613,616636.5975,523206.81,710066.385]];
  
// Threshold Profiles
var winter_avg_thresholds = [46452, 46452, 46452, 27871, 27871, 27871, 65033, 68130, 57807, 78453];
var winter_twothirds_tresholds = [60208, 60208, 60208, 36124, 36124, 36124, 84291, 88305, 74925, 101685];
// TODO - change summer thresholds
var summer_avg_thresholds = [50515, 50515,	50515,	30309, 70722,	30309,	30309,	74089, 62864, 85315];
var summer_twothirds_thresholds = [60059, 60059, 60059, 36035, 84083, 36035, 36035, 88087, 74740, 101433];

// Data Profile Options
var isWinter = true;
var aggressive = false;
var weekProfile = false;
// var csv_paths = ["../data_profiles/winter_day.csv", "../data_profiles/winter_week.csv", "../data_profiles/summer_day.csv", "../data_profiles/summer_week.csv"]

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
// function getCSV(){
//   var csv_path = "";
//   var input = [];
  
//   if(isWinter){
//     if(weekProfile){
//       csv_path = csv_paths[1];   
//     }
//     else{
//       csv_path = csv_paths[0];
//     }
//   }
//   else{
//     if(weekProfile){
//       csv_path = csv_paths[3];
//     }
//     else{
//       csv_path = csv_paths[2];
//     }
//   }

//   obj.from.path(csv_path).to.array(function (data){
//     for (var i = 0; i < data.length; i++){
//       record = [];
      
//       for(var x = 0; x < 10; x++){
//         record.push(data[i][x]);
//       }
//       for(var y = 9; y < 20; y++){
//         record.push(data[i][y]);
//       }

//       input.push(record);
//     }
//   });

//   return input;
// }

// function writeToCSV(){
//   var dataOut = [];
  
//   writer.pipe(fs.createWriteStream('out.csv'));
//   for(i = 0; i < smartMeterListStates.length; i++){
//     dataOutState = smartMeterListStates[i];

//     for(x = 0; x < dataOutState.length; x++){
//       writer.write(dataOutState[x]);
//     }
//     // obj.from.array(dataOut)
//   }

//   writer.end();
// }

// CSV Out
// Write smartMeterListStates to csv

// Run single cycle
function runCycle(generation_updates, consumption_updates) {
  updateMetersBC(generation_updates, consumption_updates);

  // Combined update and sync on contract - reduction in number of API calls
  // MicrogridContract.deployed().then(function(instance) {
  //   instance.syncMeters();
  // }).catch(function(err) {
  //   console.log(err.message);
  // });

  // refreshData();
}

function btnRunSimulation(){
  // runCycles(getCSV());
  if(isWinter){
    if(weekProfile){
      runCycles(fiveDayWinter);
    }
    else{
      runCycles(dayWinter);
    }
  }
  else{
    if(weekProfile){
      runCycles(fiveDaySummer);
    }
    else{
      runCycles(daySummer);
    }
  }
  // console.log(smartMeterListStates[smartMeterListStates.length-1]);
}

// Multiple Cycles
function runCycles(input){
  var generationUpdates = [];
  var consumptionUpdates = [];

  // setTimeout(function_x(), timeoutInMilliseconds);
  console.log(input.length);
  
  for (i = 0; i < input.length; i++){
    generationUpdates = [];
    consumptionUpdates = [];

    // console.log(i);

    for (x = 0; x < 10; x++){
      generationUpdates.push(input[i][x]);
    }

    for (y = 10; y < 20; y++){
      consumptionUpdates.push(input[i][y]);
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
  
  // var grid_size = 9;
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

  // TODO: investigate why for loop does not work as intended, instead calling the same smart meter
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

  // for (i = 0; i < smartMeterList.length; i++){
  //   MicrogridContract.deployed().then(function(instance) {
  //     return instance.getSmartMeter.call(i);
  //   }).then(function(result) {
  //     smartMeterList[i]["threshold"] = result[2];
  //     smartMeterList[i]["energy_generated"] = result[4];
  //     smartMeterList[i]["energy_sold"] = result[5];
  //     smartMeterList[i]["current_balance"] = result[6];
  //     smartMeterList[i]["available"] = result[7];
  //     console.log(result);
  //   }).catch(function(err) {
  //     console.log(err.message);
  //   });
  // }
}

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

// Update with generation and consumption update arrays
function updateMetersBC(generation_updates, consumption_updates){
  MicrogridContract.deployed().then(function(instance) {
    instance.updateSmartMeters(generation_updates, consumption_updates);
  }).catch(function(err) {
    console.log(err.message);
  });
}

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
    // console.log(totalSupply);
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

  if(isItWinter){
    if(isWeek){
      console.log("fiveDayWinter");
    }
    else{
      console.log("dayWinter");
    }
  }
  else{
    if(isWeek){
      console.log("fiveDaySummer");
    }
    else{
      console.log("daySummer");
    }
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

// When the page loads, this will call the init() function
$(function() {
  $(window).load(function() {
    init();
  });
});