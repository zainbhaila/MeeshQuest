package cmsc420.meeshquest.part2;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

/**
 * Skeleton implementation of MeeshQuest, Part 1, CMSC 420-0201, Fall 2019.
 * This does the following: (1) opens the input/output files;
 * (2) validates and parses the xml input;
 * (3) iterates through the input command nodes, but doesn't do anything
 * (4) prints the results.
 */
public class MeeshQuest {

// --------------------------------------------------------------------------------------------
//  Uncomment these to read from standard input and output (USE THESE FOR YOUR FINAL SUBMISSION)
//	private static final boolean USE_STD_IO = true; 
//	private static String inputFileName = "";
//	private static String outputFileName = "";
// --------------------------------------------------------------------------------------------
//  Uncomment these to read from a file (USE THESE FOR YOUR TESTING ONLY)
	private static final boolean USE_STD_IO = false;
	private static String inputFileName = "test/mytest-input-5-del.xml";
	private static String outputFileName = "test/mytest-output.xml";
// --------------------------------------------------------------------------------------------

	public static void main(String[] args) {

		// configure to read from file rather than standard input/output
		if (!USE_STD_IO) {
			try {
				System.setIn(new FileInputStream(inputFileName));
				System.setOut(new PrintStream(outputFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// results will be stored here
		Document results = null;
		try {
			// generate XML document for results
			results = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList<City> clist = new ArrayList<City>();
		BinarySearchTree<City> names = new BinarySearchTree<City>(); 
		SGTree<City> cmap = new SGTree<City>();

		try {
			// validate and parse XML input
			Document input = XmlUtility.validateNoNamespace(System.in);
			// create results node
			Element root = results.createElement("results");
			results.appendChild(root);
			// get input document root node
			Element rootNode = input.getDocumentElement();
			// get list of all nodes in document
			final NodeList nl = rootNode.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				// process only commands (ignore comments)
				if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
					// get next command to process
					Element command = (Element) nl.item(i); // (ignore warning - just a skeleton)if (command.getNodeName().equals("createCity")) {

					if (command.getNodeName().equals("createCity")) {
						// parse input command
						String name = command.getAttribute("name"); 
						String x = command.getAttribute("x"); 
						String y = command.getAttribute("y"); 
						String radius = command.getAttribute("radius");
						String color = command.getAttribute("color"); 
						
						// check if duplicate names and records exist
						boolean dupName = false;
						boolean dupCoord = false;
						for (City cit : clist) {
							if (cit.name.compareTo(name) == 0) {
								dupName = true;
							}
							if (cit.x.compareTo(x) == 0 && cit.y.compareTo(y) == 0) {
								dupCoord = true;
								break;
							}
						}
						
						if (dupCoord || dupName) { // duplicate name or coords
							Element error = results.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", dupCoord ? "duplicateCityCoordinates" : "duplicateCityName");
							Element comm = results.createElement("command");
							error.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							error.appendChild(parameters);
							Element n = results.createElement("name");
							n.setAttribute("value", name);
							parameters.appendChild(n);
							Element xc = results.createElement("x");
							xc.setAttribute("value", x);
							parameters.appendChild(xc);
							Element yc = results.createElement("y");
							yc.setAttribute("value", y);
							parameters.appendChild(yc);
							Element r = results.createElement("radius");
							r.setAttribute("value", radius);
							parameters.appendChild(r);
							Element c = results.createElement("color");
							c.setAttribute("value", color);
							parameters.appendChild(c);
						}
						else {
							// append output to results
							Element success = results.createElement("success");
							root.appendChild(success);
							Element comm = results.createElement("command");
							success.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							success.appendChild(parameters);
							Element n = results.createElement("name");
							n.setAttribute("value", name);
							parameters.appendChild(n);
							Element xc = results.createElement("x");
							xc.setAttribute("value", x);
							parameters.appendChild(xc);
							Element yc = results.createElement("y");
							yc.setAttribute("value", y);
							parameters.appendChild(yc);
							Element r = results.createElement("radius");
							r.setAttribute("value", radius);
							parameters.appendChild(r);
							Element c = results.createElement("color");
							c.setAttribute("value", color);
							parameters.appendChild(c);
							Element output = results.createElement("output");
							success.appendChild(output);
							City added = new City(name, String.valueOf(Integer.parseInt(x)), String.valueOf(Integer.parseInt(y)), color, String.valueOf(Integer.parseInt(radius)));
							clist.add(added);
							names.insert(added);
							cmap.insert(added);
						}
					}
					else if (command.getNodeName().equals("listCities")) {
						// parse input command
						String sortBy = command.getAttribute("sortBy");
						
						if (clist.size() == 0) {
							Element error = results.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "noCitiesToList");
							Element comm = results.createElement("command");
							comm.setAttribute("name", command.getNodeName());
							error.appendChild(comm);
							Element parameters = results.createElement("parameters");
							error.appendChild(parameters);
							Element sB = results.createElement("sortBy");
							sB.setAttribute("value", sortBy);
							parameters.appendChild(sB);
						}
						else {
							// append output to results
							Element success = results.createElement("success");
							root.appendChild(success);
							Element comm = results.createElement("command");
							success.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							success.appendChild(parameters);
							Element sB = results.createElement("sortBy");
							sB.setAttribute("value", sortBy);
							parameters.appendChild(sB);
							Element output = results.createElement("output");
							success.appendChild(output);
							Element cityList = results.createElement("cityList");
							
							// sortBy for all cities
							if (sortBy.compareTo("name") == 0) {
								clist = names.inOrderTraversal();
							}
							else {
								clist = cmap.inOrderTraversal();
							}
							
							// add cities to city list
							for (City city : clist) {
								Element el = results.createElement("city");
								el.setAttribute("name", city.name);
								el.setAttribute("x", city.x);
								el.setAttribute("y", city.y);
								el.setAttribute("radius", city.radius);
								el.setAttribute("color", city.color);
								cityList.appendChild(el);
							}
							
							output.appendChild(cityList);
						}
					}
					else if (command.getNodeName().equals("deleteCity")) {
						// parse input command
						String name = command.getAttribute("name");

						City found = null;
						for (City cit : clist) {
							if (cit.name.compareTo(name) == 0) {
								found = cit;
								clist.remove(cit);
								names.delete(cit);
								cmap.delete(cit);
								break;
							}
						}
						
						if (found == null) { // city was not found
							Element error = results.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "cityDoesNotExist");
							Element comm = results.createElement("command");
							comm.setAttribute("name", command.getNodeName());
							error.appendChild(comm);
							Element parameters = results.createElement("parameters");
							error.appendChild(parameters);
							Element sB = results.createElement("name");
							sB.setAttribute("value", name);
							parameters.appendChild(sB);
						}
						else {
							// append output to results
							Element success = results.createElement("success");
							root.appendChild(success);
							Element comm = results.createElement("command");
							success.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							success.appendChild(parameters);
							Element n = results.createElement("name");
							n.setAttribute("value", name);
							parameters.appendChild(n);
							Element output = results.createElement("output");
							Element el = results.createElement("cityDeleted");
							el.setAttribute("name", found.name);
							el.setAttribute("x", found.x);
							el.setAttribute("y", found.y);
							el.setAttribute("radius", found.radius);
							el.setAttribute("color", found.color);
							output.appendChild(el);
							success.appendChild(output);
						}
						
					}
					else if (command.getNodeName().equals("clearAll")) {
						clist = new ArrayList<City>();
						names= new BinarySearchTree<City>();
						cmap = new SGTree<City>();
						
						// append output to results
						Element success = results.createElement("success");
						root.appendChild(success);
						Element comm = results.createElement("command");
						success.appendChild(comm);
						comm.setAttribute("name", command.getNodeName());
						Element parameters = results.createElement("parameters");
						success.appendChild(parameters);
						Element output = results.createElement("output");
						success.appendChild(output);
					}
					else if (command.getNodeName().equals("printBinarySearchTree")) {
						if (names.height() == -1) { // no cities in tree
							Element error = results.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "mapIsEmpty");
							Element comm = results.createElement("command");
							comm.setAttribute("name", command.getNodeName());
							error.appendChild(comm);
							Element parameters = results.createElement("parameters");
							error.appendChild(parameters);
						}
						else {
							// append output to results
							Element success = results.createElement("success");
							root.appendChild(success);
							Element comm = results.createElement("command");
							success.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							success.appendChild(parameters);
							Element output = results.createElement("output");
							Element bst = results.createElement("binarysearchtree");
							names.printing(results, bst);
							output.appendChild(bst);
							success.appendChild(output);
						}
					}
					else if (command.getNodeName().equals("printSGTree")) {
						if (cmap.isEmpty()) { // no cities in tree
							Element error = results.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "mapIsEmpty");
							Element comm = results.createElement("command");
							comm.setAttribute("name", command.getNodeName());
							error.appendChild(comm);
							Element parameters = results.createElement("parameters");
							error.appendChild(parameters);
						}
						else {
							// append output to results
							Element success = results.createElement("success");
							root.appendChild(success);
							Element comm = results.createElement("command");
							success.appendChild(comm);
							comm.setAttribute("name", command.getNodeName());
							Element parameters = results.createElement("parameters");
							success.appendChild(parameters);
							Element output = results.createElement("output");
							Element sgt = results.createElement("SGTree");
							cmap.printing(results, sgt);
							output.appendChild(sgt);
							success.appendChild(output);
						}
					}
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			Element root = results.createElement("fatalError");
			results.appendChild(root);

		} finally {
			try {
				// print the contents of your results document
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}
}
