package com.alimoradi.elitefilebrowser.addAccount

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class AddAccountViewModel: ViewModel() {


    /*private fun getRiskAssessmentQuestionsMock(): Resource<RiskAssessmentResponseRepoModel> {
        val jsonFileString = getJsonDataFromAsset(context, "userSignalDataResponse.json")
        val gson = Gson()
        val riskAssessmentPagesType = object : TypeToken<RiskAssessmentResponseRemoteModel>() {}.type
        val riskAssessmentPages: RiskAssessmentResponseRemoteModel = gson.fromJson(jsonFileString, riskAssessmentPagesType)
        return  Resource.success(riskAssessmentResponseRepoMapper.map(riskAssessmentPages) , 200)

    }*/

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

}