package maker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class KeyTerms {

	private String term;
	private boolean required;
	private BooleanProperty filtered = new SimpleBooleanProperty() ;
	
	public KeyTerms(String keyTerm, boolean req) {
		setTerm(keyTerm);
		setRequired(req);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
		setFiltered(required);
	}

    public final BooleanProperty filteredProperty() {
        return this.filtered;
    } 
    
    public final void setFiltered(final boolean filtered) {
        this.filteredProperty().set(filtered);
    }
	
    public boolean isFiltered(boolean on) {
    	if (on) {
    		return this.filteredProperty().get();
    	} else {
    		return true;
    	}
	}

}
