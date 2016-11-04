/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

/**
 *
 * @author Ashton
 */
public class Parser {
    public static Node oneLineXmlToNode(String oneLineXml)
    {
        String response = oneLineXml;
        response = response.substring(21);  // takes out <? ... ?>
        Node tree = null, curNode = null;
        String temp0 = "";
        int i=0;
        while(i<response.length())
        {
            if(response.charAt(i) == '<')
            {
                i++;
                if(response.charAt(i) == '/')
                {
                    while(response.charAt(i) != '>') i++;
                    i++;
                    curNode = curNode.parent;
                }
                else
                {
                    temp0 = "";
                    while(response.charAt(i) != '>')
                    {
                        temp0 += response.charAt(i);
                        i++;
                    }
                    
                    Node n = new Node(temp0, curNode);
                    
                    if(null != curNode) curNode.subNodes.put(temp0, n);
                    if(null == tree) tree = n;
                    curNode = n;
                    i++;
                }
            }
            else
            {
                temp0 = "";
                while(response.charAt(i) != '<')
                {
                    temp0 += response.charAt(i);
                    i++;
                }
                curNode.element = temp0;
            }
        }
        return tree;
    }
    
}
