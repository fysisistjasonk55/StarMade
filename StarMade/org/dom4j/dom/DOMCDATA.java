package org.dom4j.dom;

import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.tree.DefaultCDATA;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DOMCDATA
  extends DefaultCDATA
  implements CDATASection
{
  public DOMCDATA(String text)
  {
    super(text);
  }
  
  public DOMCDATA(Element parent, String text)
  {
    super(parent, text);
  }
  
  public boolean supports(String feature, String version)
  {
    return DOMNodeHelper.supports(this, feature, version);
  }
  
  public String getNamespaceURI()
  {
    return DOMNodeHelper.getNamespaceURI(this);
  }
  
  public String getPrefix()
  {
    return DOMNodeHelper.getPrefix(this);
  }
  
  public void setPrefix(String prefix)
    throws DOMException
  {
    DOMNodeHelper.setPrefix(this, prefix);
  }
  
  public String getLocalName()
  {
    return DOMNodeHelper.getLocalName(this);
  }
  
  public String getNodeName()
  {
    return "#cdata-section";
  }
  
  public String getNodeValue()
    throws DOMException
  {
    return DOMNodeHelper.getNodeValue(this);
  }
  
  public void setNodeValue(String nodeValue)
    throws DOMException
  {
    DOMNodeHelper.setNodeValue(this, nodeValue);
  }
  
  public Node getParentNode()
  {
    return DOMNodeHelper.getParentNode(this);
  }
  
  public NodeList getChildNodes()
  {
    return DOMNodeHelper.getChildNodes(this);
  }
  
  public Node getFirstChild()
  {
    return DOMNodeHelper.getFirstChild(this);
  }
  
  public Node getLastChild()
  {
    return DOMNodeHelper.getLastChild(this);
  }
  
  public Node getPreviousSibling()
  {
    return DOMNodeHelper.getPreviousSibling(this);
  }
  
  public Node getNextSibling()
  {
    return DOMNodeHelper.getNextSibling(this);
  }
  
  public NamedNodeMap getAttributes()
  {
    return null;
  }
  
  public Document getOwnerDocument()
  {
    return DOMNodeHelper.getOwnerDocument(this);
  }
  
  public Node insertBefore(Node newChild, Node refChild)
    throws DOMException
  {
    checkNewChildNode(newChild);
    return DOMNodeHelper.insertBefore(this, newChild, refChild);
  }
  
  public Node replaceChild(Node newChild, Node oldChild)
    throws DOMException
  {
    checkNewChildNode(newChild);
    return DOMNodeHelper.replaceChild(this, newChild, oldChild);
  }
  
  public Node removeChild(Node oldChild)
    throws DOMException
  {
    return DOMNodeHelper.removeChild(this, oldChild);
  }
  
  public Node appendChild(Node newChild)
    throws DOMException
  {
    checkNewChildNode(newChild);
    return DOMNodeHelper.appendChild(this, newChild);
  }
  
  private void checkNewChildNode(Node newChild)
    throws DOMException
  {
    throw new DOMException((short)3, "CDATASection nodes cannot have children");
  }
  
  public boolean hasChildNodes()
  {
    return DOMNodeHelper.hasChildNodes(this);
  }
  
  public Node cloneNode(boolean deep)
  {
    return DOMNodeHelper.cloneNode(this, deep);
  }
  
  public void normalize()
  {
    DOMNodeHelper.normalize(this);
  }
  
  public boolean isSupported(String feature, String version)
  {
    return DOMNodeHelper.isSupported(this, feature, version);
  }
  
  public boolean hasAttributes()
  {
    return DOMNodeHelper.hasAttributes(this);
  }
  
  public String getData()
    throws DOMException
  {
    return DOMNodeHelper.getData(this);
  }
  
  public void setData(String data)
    throws DOMException
  {
    DOMNodeHelper.setData(this, data);
  }
  
  public int getLength()
  {
    return DOMNodeHelper.getLength(this);
  }
  
  public String substringData(int offset, int count)
    throws DOMException
  {
    return DOMNodeHelper.substringData(this, offset, count);
  }
  
  public void appendData(String arg)
    throws DOMException
  {
    DOMNodeHelper.appendData(this, arg);
  }
  
  public void insertData(int offset, String arg)
    throws DOMException
  {
    DOMNodeHelper.insertData(this, offset, arg);
  }
  
  public void deleteData(int offset, int count)
    throws DOMException
  {
    DOMNodeHelper.deleteData(this, offset, count);
  }
  
  public void replaceData(int offset, int count, String arg)
    throws DOMException
  {
    DOMNodeHelper.replaceData(this, offset, count, arg);
  }
  
  public Text splitText(int offset)
    throws DOMException
  {
    if (isReadOnly()) {
      throw new DOMException((short)7, "CharacterData node is read only: " + this);
    }
    String text = getText();
    int length = text != null ? text.length() : 0;
    if ((offset < 0) || (offset >= length)) {
      throw new DOMException((short)1, "No text at offset: " + offset);
    }
    String start = text.substring(0, offset);
    String rest = text.substring(offset);
    setText(start);
    Element parent = getParent();
    CDATA newText = createCDATA(rest);
    if (parent != null) {
      parent.add(newText);
    }
    return DOMNodeHelper.asDOMText(newText);
  }
  
  protected CDATA createCDATA(String text)
  {
    return new DOMCDATA(text);
  }
}


/* Location:           C:\Users\Raul\Desktop\StarMadeDec\StarMadeR.zip
 * Qualified Name:     org.dom4j.dom.DOMCDATA
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */