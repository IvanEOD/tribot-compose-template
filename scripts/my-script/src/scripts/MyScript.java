package scripts;

import lombok.val;
import org.tribot.script.sdk.Log;
import org.tribot.script.sdk.script.TribotScript;
import org.tribot.script.sdk.script.TribotScriptManifest;

import static scripts.kt.gui.components.BaseGuiKt.ScriptGui;

@TribotScriptManifest(name = "MyScript", author = "High Order", category = "Template", description = "My example script")
public class MyScript implements TribotScript {
	
	@Override
	public void execute(final String args) {
		
		val results = ScriptGui(MyScriptGuiKt.getMyScriptGui());
		
		Log.info("Results: " + results);
		
	}

}
