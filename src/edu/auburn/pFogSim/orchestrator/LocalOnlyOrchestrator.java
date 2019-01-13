/**
 * Centralized Orchestrator for comparison against Puddle algorithm
 * 
 * This orchestrator uses the centralized approach to selecting a VM but never associates a task to the cloud
 * @author jih0007@auburn.edu
 */
package edu.auburn.pFogSim.orchestrator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import edu.auburn.pFogSim.Radix.DistRadix;
import edu.auburn.pFogSim.netsim.ESBModel;
import edu.auburn.pFogSim.netsim.NodeSim;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;

public class LocalOnlyOrchestrator extends EdgeOrchestrator {

	ArrayList<EdgeHost> hosts;
	
	public LocalOnlyOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}
	/**
	 * get all the hosts in the network into one list
	 */
	@Override
	public void initialize() {
		hosts = new ArrayList<EdgeHost>();
		for (Datacenter node : SimManager.getInstance().getLocalServerManager().getDatacenterList()) {
			// Shaik modified - changed level 1 to level 0 for local-only
			//Qian change it back to 1
			//For change
			if (((EdgeHost) node.getHostList().get(0)).getLevel() == 1) {
				hosts.add(((EdgeHost) node.getHostList().get(0)));
			}
		}

	}
	/**
	 * get the id of the appropriate host
	 */
	@Override
	public int getDeviceToOffload(Task task) {
		try {
			return getHost(task).getId();
		}
		catch (NullPointerException e) {
			return -1;
		}
	}
	/**
	 * the the appropriate VM to run on
	 *
	 */
	@Override
	public EdgeVM getVmToOffload(Task task) {
		try {
			EdgeVM tempVM = ((EdgeVM) getHost(task).getVmList().get(0));
			return tempVM;
			
		}
		catch (NullPointerException e) {
			return null;
		}
	}
//	/**
//	 * find the host
//	 * @param task
//	 * @return
//	 */
//	private EdgeHost getHost(Task task) {
//		DistRadix sort = new DistRadix(hosts, task.getSubmittedLocation());//use radix sort based on distance from task
//		LinkedList<EdgeHost> nodes = sort.sortNodes();
//		//Shaik modified - to select the farthest fog node, instead of nearest one - EdgeHost host = nodes.poll();
//		//EdgeHost host = nodes.peekLast();
//		//Shaik modified - to select the nearest (local) fog node; doesn't remove the previously selected node; thus allowing multiple tasks generated by a given mobile device can be executed at the same host fog node - EdgeHost host = nodes.poll();
//		EdgeHost host = nodes.peekFirst();
//
//		if(goodHost(host, task)) {
//			//Shaik added following to track the target fog node on which the task is being executed.
//			//SimLogger.printLine("Mobile Device Id: "+task.getMobileDeviceId() + "  Task execution Fog Node wlan_id: " + host.getLocation().getServingWlanId());
//			//Qian get host location
//			//SimLogger.printLine("Host location X:" + host.getLocation().getXPos() + " Y:" + host.getLocation().getYPos());
//			//Qian print task location 
//			//SimLogger.printLine("Task location X:" + task.getSubmittedLocation().getXPos() + " Y:" + task.getSubmittedLocation().getYPos());
//			NodeSim des = ((ESBModel)SimManager.getInstance().getNetworkModel()).getNetworkTopology().findNode(SimManager.getInstance().getLocalServerManager().findHostById(host.getId()).getLocation(), false);
//			NodeSim src = ((ESBModel)SimManager.getInstance().getNetworkModel()).getNetworkTopology().findNode(SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()), false);
//			LinkedList<NodeSim> path = ((ESBModel)SimManager.getInstance().getNetworkModel()).findPath(src, des);
//			task.setPath(path);
//			return host;
//		}
//		return null;
//	}
	/**
	 * find the host
	 * @author Qian
	 * @return the local host (as same as the submit host)
	 */
	private EdgeHost getHost(Task task) {
		for (Datacenter node : SimManager.getInstance().getLocalServerManager().getDatacenterList()) {
			if (((EdgeHost) node.getHostList().get(0)).getLocation().getXPos() == task.getSubmittedLocation().getXPos() 
					&& ((EdgeHost) node.getHostList().get(0)).getLocation().getYPos() == task.getSubmittedLocation().getYPos()) {
				return ((EdgeHost) node.getHostList().get(0));
			}
		}
		return null;
	}
}
