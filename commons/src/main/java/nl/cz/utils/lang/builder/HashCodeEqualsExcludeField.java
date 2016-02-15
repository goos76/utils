package nl.cz.utils.lang.builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Deze annotatie geeft aan of een attribuut gebruikt wordt tijdens de berekening van de hashCode of bij de equals vergelijking in combinatie met
 * {@link HashCodeEqualsBuilder}.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HashCodeEqualsExcludeField {
	// Marker interface.
}