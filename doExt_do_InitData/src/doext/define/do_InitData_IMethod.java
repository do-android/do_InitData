package doext.define;

import org.json.JSONObject;

import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;

/**
 * 声明自定义扩展组件方法
 */
public interface do_InitData_IMethod {
	void dirExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void fileExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void readFileSync(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception;

	void copy(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void copyFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void getDirs(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void getFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void readFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void unZip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void zip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;

	void zipFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult, String _callbackFuncName) throws Exception;
}