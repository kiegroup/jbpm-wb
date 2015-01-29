package org.jbpm.console.ng.gc.client.cell;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class WordWrapCell extends AbstractSafeHtmlCell<String>{
	
	public WordWrapCell() {
	    super(SimpleSafeHtmlRenderer.getInstance());
	  }

	public WordWrapCell(SafeHtmlRenderer<String> renderer) {
	   super(renderer);
	  }
	
	@Override
	public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<div title = '");
			sb.append(value);
			sb.appendHtmlConstant("' style='white-space: normal;'>");
			sb.append(value);
			sb.appendHtmlConstant("</div>");
	    }
	  }
}
