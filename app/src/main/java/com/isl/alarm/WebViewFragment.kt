//package com.isl.alarm
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.fragment.app.Fragment
//import org.mozilla.geckoview.GeckoRuntime
//import org.mozilla.geckoview.GeckoSession
//import org.mozilla.geckoview.GeckoView
//class GeckoViewFragment(private val url: String) : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return ComposeView(requireContext()).apply {
//            setContent {
//                GeckoViewScreen(url)
//            }
//        }
//    }
//}
//@Composable
//fun GeckoViewScreen(url: String) {
//    AndroidView(factory = { context ->
//// Create a GeckoView instance
//        val geckoView = GeckoView(context)
//// Create a new GeckoSession and open it with a runtime
//        val session = GeckoSession()
////        if (runtime==null){
////            runtime = GeckoRuntime.create(context)
////        }
//////single instance
////        session.open(runtime!!)
//        geckoView.setSession(session)
//// OPTIONAL: Set a delegate to monitor page loading progress and security changes
//        session.progressDelegate = object : GeckoSession.ProgressDelegate {
//            override fun onPageStart(session: GeckoSession, url: String) {
//// Called when a new page starts loading
//            }
//            override fun onPageStop(session: GeckoSession, success: Boolean) {
//// Called when page load finishes
//            }
//            override fun onProgressChange(session: GeckoSession, progress: Int) {
//// Update progress UI if needed
//            }
//            override fun onSecurityChange(
//                session: GeckoSession,
//                securityInfo: GeckoSession.ProgressDelegate.SecurityInformation
//            ) {
//// Handle security status updates if needed
//            }
//        }
//// Load the URL
//        session.loadUri(url)
//        geckoView
//    })
//}