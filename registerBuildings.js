const artifacts = require('./build/contracts/microgrid.json');
const contract = require('truffle-contract');
const MyContract = contract(artifacts);
var names = ["Gartland 1", "Gartland 2", "Fontaine", "Foy 1", "Foy 2", "Foy 3", "Dyson", "Lowell-Thomas", "Hancock", "Library"];
var winter_avg_thresholds = [46452, 46452, 46452, 27871, 27871, 27871, 65033, 68130, 57807, 78453];

MyContract.setProvider(web3.currentProvider);

MyContract.deployed().then(function(instance){
    console.log(instance.address);
    // console.log(web3.eth.accounts[0]);
    // for (i = 0; i < names.length; i++){
    instance.registerSmartMeter(0, web3.eth.accounts[0], winter_avg_thresholds[0], names[0], {from: web3.eth.accounts[0]});
    // }
}).catch(function(error){
    console.error(error);
});