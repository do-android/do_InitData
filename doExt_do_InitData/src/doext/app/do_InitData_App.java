package doext.app;
import android.content.Context;
import core.interfaces.DoIAppDelegate;

/**
 * APP启动的时候会执行onCreate方法；
 *
 */
public class do_InitData_App implements DoIAppDelegate {

	private static do_InitData_App instance;
	
	private do_InitData_App(){
		
	}
	
	public static do_InitData_App getInstance() {
		if(instance == null){
			instance = new do_InitData_App();
		}
		return instance;
	}
	
	@Override
	public void onCreate(Context context) {
		// ...do something
	}
	
	@Override
	public String getTypeID() {
		return "do_InitData";
	}
}
