package org.jbpm.console.ng.gc.client.cell;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class longContentCell extends AbstractSafeHtmlCell<String>{
	
	private boolean isWordWrap = false; 
	
	public longContentCell() {
	    super(SimpleSafeHtmlRenderer.getInstance());
	  }
	
	public longContentCell (final boolean isWordWrap ) {
		 super(SimpleSafeHtmlRenderer.getInstance());
		 this.isWordWrap = isWordWrap;
	   }

	public longContentCell(SafeHtmlRenderer<String> renderer) {
	   super(renderer);
	  }
	
	@Override
	public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
		if (isWordWrap){
			renderWithWordWrap(value, sb);
		}else{
			renderWithoutWordWrap(value, sb);
		}
	  }

	private void renderWithWordWrap(SafeHtml value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<div title = '");
			sb.append(value);
			sb.appendHtmlConstant("' style='white-space: normal;'>");
			sb.append(value);
			sb.appendHtmlConstant("</div>");
	    }
	}
	
	private void renderWithoutWordWrap(SafeHtml value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<div title = '");
			sb.append(value);
			sb.appendHtmlConstant("'>");
			sb.append(value);
			sb.appendHtmlConstant(" </div>");
	    }
	}
}
