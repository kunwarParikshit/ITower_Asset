package com.isl.assetManagement.constants

class DefaultLevel {

    companion object {
        fun msg(): HashMap<String, String> {
            val level = HashMap<String, String>()
            level["assetTab"] = "Assigned,Raised,Rejected,Closed"
            level["assetStatus"] = "Closed=Closed," +
                    "Rejected=Rejected," +
                    "Pending_for_Sender=Dispatch Pending," +
                    "Pending_for_Receiver=Installation Pending," +
                    "Open=Waiting Approval-1," +
                    "Pending_for_2nd_Approver=Waiting Approval-2," +
                    "Pending_for_3nd_Approver=Waiting Approval-3," +
                    "dateFormate=dd-MM-yyyy"  // 02-Dec-2024
            level["qty"] = "Password"
            level["3"] = "Forgot Password"
            level["14"] = "data base work"
            return level
        }
    }
}

//val messageMap = DefaultLevel.msg()

// Access a value using a key
//val value = messageMap["1"]
