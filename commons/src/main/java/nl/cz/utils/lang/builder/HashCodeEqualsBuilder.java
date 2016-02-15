package nl.cz.utils.lang.builder;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Deze klasse is verantwoordelijk voor de implementatie van de de hashCode calculatie, de equals vergelijking van objecten, en de toString van een
 * object met behulp van de volgende klasse uit apache commons.
 * <ol>
 * <li>{@link HashCodeBuilder#reflectionHashCode(Object, java.util.Collection)}</li>
 * <li>{@link EqualsBuilder#reflectionEquals(Object, Object, java.util.Collection)}</li>
 * <li>{@link ToStringBuilder#reflectionToString(Object, ToStringStyle)}</li>
 * </ol>
 * De {@link HashCodeEqualsExcludeField} geannoteerde attributen worden niet meegenomen in de calculatie van de hashCode en in de equals vergelijking.
 * </br></br> Voor 't opbouwen van de string in de toString geldt dat:
 * <ol>
 * <li>De {@link ToStringExcludeField} geannoteerde attributen worden <b>niet</b> meegenomen</li>
 * <li>De {@link ToStringIncludeField} geannoteerde attributen worden <b>wel</b> meegenomen</li>
 * </ol>
 * 
 * 
 */
public class HashCodeEqualsBuilder {

	private static final HashSet<Class<?>> TO_STRING_TYPES = new HashSet<>();

	static {
		TO_STRING_TYPES.add(String.class);
		TO_STRING_TYPES.add(Integer.class);
		TO_STRING_TYPES.add(Double.class);
		TO_STRING_TYPES.add(Long.class);
		TO_STRING_TYPES.add(BigDecimal.class);
		TO_STRING_TYPES.add(java.sql.Date.class);
		TO_STRING_TYPES.add(java.util.Date.class);
		TO_STRING_TYPES.add(Timestamp.class);
		
	}

	/**
	 * Deze methode voert een equals vergelijking. De {@link HashCodeEqualsExcludeField} geannoteerde attributen worden niet meegenomen in de equals
	 * vergelijking.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 * 
	 */
	public static boolean equals(Object lhs, Object rhs) {
		ArrayList<String> excludeFields = getHashCodeEqualsExcludeFields(lhs);
		return EqualsBuilder.reflectionEquals(lhs, rhs, excludeFields);
	}

	/**
	 * Deze methode berekent de hashcode van een object. De {@link HashCodeEqualsExcludeField} geannoteerde attributen worden niet meegenomen in de
	 * calculatie.
	 * 
	 * @param object
	 * @return
	 * 
	 */
	public static int hashCode(Object object) {
		ArrayList<String> excludeFields = getHashCodeEqualsExcludeFields(object);
		return HashCodeBuilder.reflectionHashCode(object, excludeFields);
	}

	/**
	 * Deze methode retourneert een string samengesteld uit de attributen op basis van de volgende annotaties {@link ToStringIncludeField} en
	 * {@link ToStringExcludeField}. Default wordt de string alleen opgebouwd uit de attributen van de volgende types.
	 * <ul>
	 * <li>String</li>
	 * <li>Integer</li>
	 * <li>Double</li>
	 * <li>Long</li>
	 * <li>BigDecimal</li>
	 * <li>java.sql.Date</li>
	 * <li>java.util.Date</li>
	 * <li>Timestamp</li>
	 * <li>Bedrag</li>
	 * <li>ObjectId</li>
	 * <li>FunctioneleSleutel</li>
	 * <li>Banknummer</li>
	 * <li>ITypeSafeEnum</li>
	 * </ul>
	 * 
	 * @param object
	 * @return
	 */
	public static String toString(Object object) {
		ArrayList<String> excludeFields = getToStringExcludeFields(object);
		ReflectionToStringBuilder toStringBuilder = new ReflectionToStringBuilder(object, ToStringStyle.MULTI_LINE_STYLE)
				.setExcludeFieldNames(excludeFields.toArray(new String[excludeFields.size()]));

		return toStringBuilder.toString();

	}

	/**
	 * Deze methode vergelijkt een object met een ander object. De {@link HashCodeEqualsExcludeField} geannoteerde attributen worden niet meegenomen
	 * in de vergelijking.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static int compare(Object lhs, Object rhs) {
		ArrayList<String> excludeFields = getHashCodeEqualsExcludeFields(lhs);
		return CompareToBuilder.reflectionCompare(lhs, rhs, excludeFields);
	}

	private static ArrayList<String> getHashCodeEqualsExcludeFields(Object object) {
		ArrayList<String> excludeFields = new ArrayList<>();
		if (object != null) {
			Class<?> objectClass = object.getClass();

			for (Field field : objectClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(HashCodeEqualsExcludeField.class)) {
					excludeFields.add(field.getName());
				}
			}
		}
		return excludeFields;
	}

	private static ArrayList<String> getToStringExcludeFields(Object object) {
		ArrayList<String> allExcludeFields = new ArrayList<>();
		if (object != null) {
			ArrayList<String> excludeFields = getToStringExcludeFields(object.getClass());
			allExcludeFields.addAll(excludeFields);

		}
		return allExcludeFields;
	}

	private static ArrayList<String> getToStringExcludeFields(Class<?> objectClass) {
		ArrayList<String> excludeFields = new ArrayList<>();
		for (Field field : objectClass.getDeclaredFields()) {
			if (isToStringExclude(field)) {
				excludeFields.add(field.getName());
			}
		}
		Class<?> superClass = objectClass.getSuperclass();
		if (superClass != null) {
			ArrayList<String> excludeFieldsSuperClass = getToStringExcludeFields(superClass);
			excludeFields.addAll(excludeFieldsSuperClass);
		}
		return excludeFields;
	}

	private static boolean isToStringExclude(Field field) {
		Class<?> type = field.getType();
		if (field.isAnnotationPresent(ToStringExcludeField.class)) {
			return true;
		}
		if (field.isAnnotationPresent(ToStringIncludeField.class)) {
			return false;
		}
		if (type.isPrimitive()) {
			return false;
		}
		if (type.isEnum()) {
			return false;
		}
		if (TO_STRING_TYPES.contains(type)) {
			return false;
		}
		Class<?>[] interfaces = type.getInterfaces();
		for (Class<?> interfaceType : interfaces) {
			if (TO_STRING_TYPES.contains(interfaceType)) {
				return false;
			}
		}

		return true;

	}

}
