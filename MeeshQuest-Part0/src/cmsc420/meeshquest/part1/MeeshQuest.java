package cmsc420.meeshquest.part1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

/**
 * A skeletal version of the MeeshQuest program. This does the following: (1)
 * opens the input/output files; (2) validates and parses the xml input; (3)
 * iterates through the command nodes of the xml input, but doesn't do any
 * processing; (4) prints the results.
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
	private static String inputFileName = "tests/part1/part1.test2.input.xml";
	private static String outputFileName = "tests/part1/part1.test2.output.xml";
// --------------------------------------------------------------------------------------------

	public static void main(String[] args) {

		// configure to read from file rather than standard input/output
		if (!USE_STD_IO) {
			try {
				System.setIn(new FileInputStream(inputFileName));
				System.setOut(new PrintStream(outputFileName));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		// results will be stored here
		Document results = null;

		try {
			// validate and parse XML input
			Document input = XmlUtility.validateNoNamespace(System.in);
			// general XML document for results
			results = XmlUtility.getDocumentBuilder().newDocument();
			Element root = results.createElement("results");
			results.appendChild(root);
			// get input document root node
			Element rootNode = input.getDocumentElement();
			// get list of all nodes in document
			final NodeList nl = rootNode.getChildNodes();
			// enumerate through the commands
			for (int i = 0; i < nl.getLength(); i++) {
				// process only commands (ignore comments)
				if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
					// get next command to process
					Element command = (Element) nl.item(i); 

					if (command.getNodeName().equals("createCity")) {
						// parse input command
						String name = command.getAttribute("name"); 
						String x = command.getAttribute("x"); 
						String y = command.getAttribute("y"); 
						String radius = command.getAttribute("radius");
						String color = command.getAttribute("color"); 
						
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
					}
					else if (command.getNodeName().equals("listCities")) {
						// parse input command
						String sortBy = command.getAttribute("sortBy");
						
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
						
						// loop through all success nodes and get cities
						NodeList rootList = root.getChildNodes();
						ArrayList<Element> storage = new ArrayList<Element>();
						for (int j = 0; j < rootList.getLength(); j++) {
							if (rootList.item(j).getNodeType() == Document.ELEMENT_NODE) {
								Element temp = (Element) rootList.item(j); 
								NodeList cL = temp.getChildNodes();
								Element current = (Element) cL.item(0);
								
								// for all createCity commands
								if (current.getAttribute("name") == "createCity") {
									NodeList paramList = cL.item(1).getChildNodes();
									Element city = results.createElement("city");
									
									// get all attributes for city
									for (int k = 0; k < paramList.getLength(); k++) { 
										Element param = (Element) paramList.item(k);
										city.setAttribute(param.getNodeName(), param.getAttribute("value"));
									}
									
									storage.add(city);
								}
							}
						}
						
						// sortBy for all cities
						if (sortBy.compareTo("name") == 0) {
							storage.sort((a, b) ->{ return a.getAttribute("name").compareTo(b.getAttribute("name")); } );
						}
						else {
							Collections.sort(storage, (a, b) -> {
								if (Float.parseFloat(a.getAttribute("x")) == Float.parseFloat(b.getAttribute("x"))) {
									return Float.compare(Float.parseFloat(a.getAttribute("y")), Float.parseFloat(b.getAttribute("y")));
								}
								else {
									return Float.compare(Float.parseFloat(a.getAttribute("x")), Float.parseFloat(b.getAttribute("x")));
								}});
						}
						
						// add cities to city list
						for (Element el : storage) {
							cityList.appendChild(el);
						}
						
						output.appendChild(cityList);
					}
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {

			// -----------------------------------------------------------
			// TODO: Add your error processing here
			// -----------------------------------------------------------

		} finally {
			try {
				// print the contents of the your results document
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}
}
