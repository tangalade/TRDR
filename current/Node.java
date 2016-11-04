/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oracle;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Ashton
 */
public class Node {
    String name, element;
    
    Map<String, Node> subNodes;
    
    Node parent;
    
    public Node(String _name, Node _parent)
    {
        parent = _parent;
        name = _name;
        element = null;
        subNodes = new TreeMap<>();
    }
    
    public void outputSubtree()
    {
        _outputSubtree(this, 0);
    }
    private void _outputSubtree(Node n, int level)
    {
        for(int i=0; i<level; i++) System.out.print('-');
        System.out.println(n.name + ": " + (null == n.element ? "null" : n.element));
        for(String key : n.subNodes.keySet())
            _outputSubtree(n.subNodes.get(key), level + 1);
    }
}
