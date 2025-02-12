package com.isl.assetManagement.constants

import org.mozilla.geckoview.GeckoRuntime


object AppConstants {
    const val baseUrl = "https://mast-iasset-qc.infozech.com:8000/"  //dev

    //const val baseUrl = "https://midc-qc-iasset.infozech.com/"
    const val tokenUrl = "https://idpdev-app.infozech.com:9014/"
    const val onmUrl = "http://192.168.0.35:6007/"

    object PrefsName {
        const val deviceUUID = "deviceUUID"
        const val assetUserId = "assetUserId"

    }

    object WebUrlsAndInstance {
        const val epaCapacity =
            "https://app.powerbi.com/view?r=eyJrIjoiNzlmYTE3MGItODllNy00ZDcwLThiODEtZDBjMjAwNTc0ZTA4IiwidCI6IjYxYjBjMGJhLWVlOTEtNGFkMy05ZTVjLTMxZmU5MmI1MmFmMCJ9"
        const val incidentManagementAnalysis =
            "https://app.powerbi.com/view?r=eyJrIjoiNzE4NzNmMjQtNzQ3Ny00ZDdmLWJhMTQtYWFkMjQ1Yzc3MmRlIiwidCI6IjYxYjBjMGJhLWVlOTEtNGFkMy05ZTVjLTMxZmU5MmI1MmFmMCJ9"
        //new url // "https://app.powerbi.com/view?r=eyJrIjoiZjNhMGRiODUtMDhmYS00OWMzLTlkMGYtNmViYjA5NTJjOTIzIiwidCI6IjYxYjBjMGJhLWVlOTEtNGFkMy05ZTVjLTMxZmU5MmI1MmFmMCJ9"
        var runtime: GeckoRuntime? = null
    }
}