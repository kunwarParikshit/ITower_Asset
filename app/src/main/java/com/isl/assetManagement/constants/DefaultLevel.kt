package com.isl.assetManagement.constants

class DefaultLevel {

    companion object {
        fun msg(): HashMap<String, String> {
            val level = HashMap<String, String>()
            level["assetTab"] = "Assigned,Raised,Rejected,Closed"
            level["assetStatus"] = "Closed=Closed," +
                    "Rejected=Rejected," +
                    "Pending_for_Sender=Dispatch Pending," +
                    "PendingforIssuer=Dispatch Pending," +
                    "Pending_for_Receiver=Installation Pending," +
                    "PendingforInstaller=Installation Pending," +
                    "Open=Waiting Approval-1," +
                    "Pending_for_2nd_Approver=Waiting Approval-2," +
                    "Pending_for_3nd_Approver=Waiting Approval-3," +

                    "dateFormate=M/d/yyyy"  // 02-Dec-2024  dd-MMM-yyyy
            level["imgTag"] =
                     "0=Asset-1," +
                    "1=Asset-2," +
                    "2=Asset-3," +
                    "3=Asset-4," +
                    "4=Asset-5," +
                    "5=Asset-6," +
                    "6=Asset-7," +
                    "7=Asset-8," +
                    "8=Asset-9," +
                    "9=Asset-10," +
                    "10=Asset-11," +
                    "11=Asset-12," +
                    "12=Asset-13," +
                    "13=Asset-14," +
                    "14=Asset-15"
            level["qty"] = "Password"
            level["isTokenAuthentication"] = "0"
            level["14"] = "data base work"
            return level
        }
    }
}

//val messageMap = DefaultLevel.msg()

// Access a value using a key
//val value = messageMap["1"]
