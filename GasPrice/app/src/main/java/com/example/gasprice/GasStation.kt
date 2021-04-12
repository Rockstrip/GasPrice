package com.example.gasprice

class GasStation(name:String, region:String, prices:MutableList<Float?>){
    var Name:String = name;
    var Region:String = region;
     var A95Plus: Float? = prices[0];
     var A95: Float? = prices[1];
     var A92: Float? = prices[2];
     var DT: Float? = prices[3];
     var Gas: Float? = prices[4];

 override fun toString(): String = "\n $Region $Name \n $A95Plus $A95 $A92 $DT $Gas"
}