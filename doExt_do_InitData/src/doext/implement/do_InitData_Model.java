package doext.implement;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import core.DoServiceContainer;
import core.helper.DoIOHelper;
import core.helper.DoJsonHelper;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoISourceFS;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_InitData_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_InitData_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_InitData_Model extends DoSingletonModule implements do_InitData_IMethod {

	public do_InitData_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if ("dirExist".equals(_methodName)) {
			this.dirExist(_dictParas, _scriptEngine, _invokeResult);
			return true;
		} else if ("fileExist".equals(_methodName)) {
			this.fileExist(_dictParas, _scriptEngine, _invokeResult);
			return true;
		} else if ("readFileSync".equals(_methodName)) {
			this.readFileSync(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		DoInvokeResult _invokeResult = new DoInvokeResult(this.getUniqueKey());
		if ("getFiles".equals(_methodName)) {
			this.getFiles(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("getDirs".equals(_methodName)) {
			this.getDirs(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("readFile".equals(_methodName)) {
			this.readFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("zip".equals(_methodName)) {
			this.zip(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("unzip".equals(_methodName)) {
			this.unZip(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("copy".equals(_methodName)) {
			this.copy(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("zipFiles".equals(_methodName)) {
			this.zipFiles(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
			return true;
		} else if ("copyFile".equals(_methodName)) {
			this.copyFile(_dictParas, _scriptEngine, _invokeResult, _callbackFuncName);
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 拷贝文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void copy(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			JSONArray _sources = DoJsonHelper.getJSONArray(_dictParas, "source");
			if (_sources == null || _sources.length() <= 0) {
				throw new Exception("source不能为空!");
			}
			//检查source里面是否包含了不合法目录(不支持source://开头的目录)
			if (!checkFilePathValidate(_sources)) {
				throw new Exception("source参数只支持" + DoISourceFS.INIT_DATA_PREFIX + " 打头!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			_target = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _target);
			Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
			for (int i = 0; i < _sources.length(); i++) {
				String _fullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _sources.getString(i));
				if (DoIOHelper.isAssets(_fullPath)) {
					DoIOHelper.copyFileOrDirectory(_context, _fullPath, _target);
				} else {
					DoIOHelper.copyFileOrDirectory(_fullPath, _target);
				}
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception e) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_InitData copy /t", e);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 拷贝文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void copyFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("source参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			String _sourceFullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _source);
			if (!DoIOHelper.existFile(_sourceFullPath)) {
				throw new Exception(_source + " 文件不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}

			_target = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _target);
			if (DoIOHelper.isAssets(_sourceFullPath)) {
				Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
				DoIOHelper.copyFileByBybeBuffer(_context, _sourceFullPath, _target);
			} else {
				DoIOHelper.copyFileByBybeBuffer(_sourceFullPath, _target);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception e) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_InitData copyFile /r/n", e);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 判断目录是否存在；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void dirExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path)) {
			throw new Exception("path不能为空!");
		}
		if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
			throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
		}
		String _dirFullPath = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
		_invokeResult.setResultBoolean(DoIOHelper.existDirectory(_dirFullPath));

	}

	/**
	 * 判断文件是否存在；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void fileExist(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path)) {
			throw new Exception("path不能为空!");
		}
		if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
			throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
		}
		String _fileFullPath = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
		_invokeResult.setResultBoolean(DoIOHelper.existFile(_fileFullPath));

	}

	/**
	 * 获取目录列表；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void getDirs(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
			if (_dirFullPath.charAt(_dirFullPath.length() - 1) == '/') {
				_dirFullPath = _dirFullPath.substring(0, _dirFullPath.length() - 1);
			}

			List<String> _listAppDirs = null;
			if (DoIOHelper.isAssets(_dirFullPath)) {
				Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
				_listAppDirs = DoIOHelper.getDirectories(_context, _dirFullPath);
			} else {
				_listAppDirs = DoIOHelper.getDirectories(_dirFullPath);
			}

			JSONArray _tempArray = new JSONArray();
			for (String _dirPaht : _listAppDirs) {
				_tempArray.put(_dirPaht);
			}
			_invokeResult.setResultArray(_tempArray);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 获取文件列表；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void getFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			String _dirFullPath = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
			if (_dirFullPath.charAt(_dirFullPath.length() - 1) == '/') {
				_dirFullPath = _dirFullPath.substring(0, _dirFullPath.length() - 1);
			}
			List<String> _listAppFiles = null;
			if (DoIOHelper.isAssets(_dirFullPath)) {
				Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
				_listAppFiles = DoIOHelper.getFiles(_context, _dirFullPath);
			} else {
				_listAppFiles = DoIOHelper.getFiles(_dirFullPath);
			}
			JSONArray _tempArray = new JSONArray();
			for (String _dirPaht : _listAppFiles) {
				_tempArray.put(_dirPaht);
			}
			_invokeResult.setResultArray(_tempArray);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 读取文件内容；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void readFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		String _content = "";
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			String _fileFullName = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
			if (!DoIOHelper.existFile(_fileFullName)) {
				throw new Exception(_path + "文件不存在！");
			}
			_content = DoIOHelper.readUTF8File(_fileFullName);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_invokeResult.setResultText(_content);
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 读取文件内容；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void readFileSync(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _content = "";
		try {
			String _path = DoJsonHelper.getString(_dictParas, "path", "");
			if (TextUtils.isEmpty(_path)) {
				throw new Exception("path不能为空!");
			}
			if (!_path.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("path参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			String _fileFullName = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_path);
			if (!DoIOHelper.existFile(_fileFullName)) {
				throw new Exception(_path + "文件不存在！");
			}
			_content = DoIOHelper.readUTF8File(_fileFullName);
		} catch (Exception ex) {
			_invokeResult.setException(ex);
		} finally {
			_invokeResult.setResultText(_content);
		}
	}

	/**
	 * 解压缩文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void unZip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("source参数只支持" + DoISourceFS.INIT_DATA_PREFIX + " 打头!");
			}

			String _sourceFullPath = _scriptEngine.getCurrentApp().getSourceFS().getFileFullPathByName(_source);
			if (!DoIOHelper.existFile(_sourceFullPath)) {
				throw new Exception(_source + " 文件不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持" + DoISourceFS.DATA_PREFIX + " 打头!");
			}

			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);
			if (DoIOHelper.isAssets(_sourceFullPath)) {
				Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
				DoIOHelper.unZipAssetsFolder(_context, _sourceFullPath, _target);
			} else {
				DoIOHelper.unZipFolder(_sourceFullPath, _target);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_InitData unzip /t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	/**
	 * 压缩文件或目录；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void zip(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			String _source = DoJsonHelper.getString(_dictParas, "source", "");
			if (TextUtils.isEmpty(_source)) {
				throw new Exception("source不能为空!");
			}
			if (!_source.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				throw new Exception("source参数只支持 " + DoISourceFS.INIT_DATA_PREFIX + "打头!");
			}
			if (_source.charAt(_source.length() - 1) == '/') {
				_source = _source.substring(0, _source.length() - 1);
			}
			String _sourceFullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _source);
			if (!DoIOHelper.existDirectory(_sourceFullPath) && !DoIOHelper.existFile(_sourceFullPath)) {
				throw new Exception(_source + " 文件或者目录不存在!");
			}

			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}

			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);
			if (!DoIOHelper.existFile(_target)) {
				DoIOHelper.createFile(_target);
			}

			if (DoIOHelper.isAssets(_sourceFullPath)) {
				Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
				if (DoIOHelper.existFile(_sourceFullPath)) {
					DoIOHelper.zipAssetsFile(_context, _sourceFullPath, _target);
				} else {
					DoIOHelper.zipAssetsFolder(_context, _sourceFullPath, _target);
				}
			} else {
				File _sourceFile = new File(_sourceFullPath);
				if (_sourceFile.isDirectory()) {
					DoIOHelper.zipFolder(_sourceFile.listFiles(), _target);
				} else {
					DoIOHelper.zipFile(_sourceFile, _target);
				}
			}

			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_InitData zip \t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}
	}

	public static boolean checkFilePathValidate(JSONArray _array) throws JSONException {
		boolean _isVlidate = true;
		for (int i = 0; i < _array.length(); i++) {
			String _mFilePath = _array.getString(i);
			if (!TextUtils.isEmpty(_mFilePath) && !_mFilePath.startsWith(DoISourceFS.INIT_DATA_PREFIX)) {
				_isVlidate = false;
				break;
			}
		}
		return _isVlidate;
	}

	/**
	 * 压缩多个文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void zipFiles(JSONObject _dictParas, DoIScriptEngine _scriptEngine, final DoInvokeResult _invokeResult, String _callbackFuncName) {
		try {
			JSONArray _sources = DoJsonHelper.getJSONArray(_dictParas, "source");
			if (_sources == null || _sources.length() == 0) {
				throw new Exception("source不能为空!");
			}
			//检查source里面是否包含了不合法目录(不支持source://开头的目录)
			if (!checkFilePathValidate(_sources)) {
				throw new Exception("source参数只支持" + DoISourceFS.INIT_DATA_PREFIX + " 打头!");
			}
			String _target = DoJsonHelper.getString(_dictParas, "target", "");
			if (TextUtils.isEmpty(_target)) {
				throw new Exception("target不能为空!");
			}
			if (!_target.startsWith(DoISourceFS.DATA_PREFIX)) {
				throw new Exception("target参数只支持 " + DoISourceFS.DATA_PREFIX + "打头!");
			}
			_target = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_target);

			File _targetFile = new File(_target);
			File _parentFile = _targetFile.getParentFile();
			if (!_parentFile.exists()) {
				_parentFile.mkdirs();
			}
			// 先copy 到一个temp 目录下面
			File _tempFile = new File(_parentFile, System.currentTimeMillis() + "");
			_tempFile.mkdir();
			Context _context = DoServiceContainer.getPageViewFactory().getAppContext();
			for (int i = 0; i < _sources.length(); i++) {
				String _fullPath = DoIOHelper.getLocalFileFullPath(_scriptEngine.getCurrentApp(), _sources.getString(i));
				if (DoIOHelper.existFile(_fullPath)) {
					if (DoIOHelper.isAssets(_fullPath)) {
						DoIOHelper.copyFileOrDirectory(_context, _fullPath, _tempFile.getAbsolutePath());
					} else {
						DoIOHelper.copyFileOrDirectory(_fullPath, _tempFile.getAbsolutePath());
					}
				}
			}
			DoIOHelper.zipFolder(_tempFile.listFiles(), _target);
			// 删除临时目录
			DoIOHelper.deleteDirectory(_tempFile.getAbsolutePath());
			_invokeResult.setResultBoolean(true);
		} catch (Exception ex) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("do_InitData zipFiles /t", ex);
		} finally {
			_scriptEngine.callback(_callbackFuncName, _invokeResult);
		}

	}
}