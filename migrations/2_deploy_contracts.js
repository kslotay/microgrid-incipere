const microgrid = artifacts.require("./microgrid.sol")

module.exports = function(deployer) {
	deployer.deploy(microgrid);
};