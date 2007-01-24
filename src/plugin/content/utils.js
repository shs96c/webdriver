function dumpall(name,obj,niv) {
  if (!niv) niv=1;
  var dumpdict=new Object();

  dump ("\n\n-------------------------------------------------------\n");
  dump ("Dump of the objet: " + name + " (" + niv + " levels)\n");
  dump ("Address: " + obj + "\n");
  dump ("Interfaces: ");
  for (var i in Components.interfaces) {
    try {
      obj.QueryInterface(Components.interfaces[i]);
      dump(""+Components.interfaces[i]+", ");
    } catch (ex) {}
  }
  dump("\n");
  _dumpall(dumpdict,obj,niv,"","");
  dump ("\n\n-------------------------------------------------------\n\n");
  
  for (i in dumpdict) {
    delete dumpdict[i];
  }
}
function _dumpall(dumpdict,obj,niv,tab,path) {

  if (obj in dumpdict) {
    dump(" (Already dumped)");
  } else {
    dumpdict[obj]=1;
    
    var i,r,str,typ;
    for (i in obj) {
      try {
        str = String(obj[i]).replace(/\n/g,"\n"+tab);
      } catch (ex) {
        str = String(ex);
      }
      try {
        typ = ""+typeof(obj[i]);
      } catch (ex) {
        typ = "unknown";
      }
      dump ("\n" + tab + i + " (" + typ + (path?", " + path:"") +"): " + str);
      if ((niv>1) && (typ=="object")) {
        _dumpall(dumpdict,obj[i],niv-1,tab+"\t",(path?path+"->"+i:i));
      }
    }
  }
}