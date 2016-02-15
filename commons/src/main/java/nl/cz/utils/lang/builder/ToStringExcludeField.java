package nl.cz.utils.lang.builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Deze annotatie geeft aan of een NIET attribuut gebruikt wordt tijdens de aanroep van ToString in combinatie met {@link HashCodeEqualsBuilder}.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ToStringExcludeField {
	// Marker interface.
}