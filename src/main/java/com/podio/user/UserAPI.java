package com.podio.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.podio.BaseAPI;
import com.podio.contact.Profile;
import com.podio.contact.ProfileField;
import com.podio.contact.ProfileFieldValues;
import com.podio.contact.ProfileUpdate;
import com.sun.jersey.api.client.GenericType;

public class UserAPI {

	private final BaseAPI baseAPI;

	public UserAPI(BaseAPI baseAPI) {
		this.baseAPI = baseAPI;
	}

	/**
	 * Updates the active user. The old and new password can be left out, in
	 * which case the password will not be changed. If the mail is changed, the
	 * old password has to be supplied as well.
	 */
	public void updateUser(UserUpdate update) {
		baseAPI.getResource("/user/")
				.entity(update, MediaType.APPLICATION_JSON_TYPE).put();
	}

	/**
	 * Returns the current status for the user. This includes the user data,
	 * profile data and notification data.
	 */
	public UserStatus getStatus() {
		return baseAPI.getResource("/user/status")
				.accept(MediaType.APPLICATION_JSON_TYPE).get(UserStatus.class);
	}

	/**
	 * Returns the profile of the active user
	 */
	public Profile getProfile() {
		return baseAPI.getResource("/user/profile/")
				.accept(MediaType.APPLICATION_JSON_TYPE).get(Profile.class);
	}

	/**
	 * Returns the field of the profile for the given key from the active user.
	 */
	public <T, R> List<T> getProfileField(ProfileField<T, R> field) {
		List<R> values = baseAPI
				.getResource("/user/profile/" + field.getName())
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(new GenericType<List<R>>() {
				});

		List<T> formatted = new ArrayList<T>();
		for (R value : values) {
			formatted.add(field.parse(value));
		}

		return formatted;
	}

	/**
	 * Updates the fields of an existing profile. All fields must be filled out,
	 * as any fields not included will not be part of the new revision.
	 */
	public void updateProfile(ProfileUpdate update) {
		baseAPI.getResource("/user/profile/")
				.entity(update, MediaType.APPLICATION_JSON_TYPE).put();
	}

	/**
	 * Updates a single field on the profile of the user
	 */
	public <F> void updateProfileField(ProfileField<F, ?> field, F value) {
		if (field.isSingle()) {
			baseAPI.getResource("/user/profile/" + field.getName())
					.entity(new ProfileFieldSingleValue<F>(value),
							MediaType.APPLICATION_JSON_TYPE).put();
		} else {
			baseAPI.getResource("/user/profile/" + field.getName())
					.entity(new ProfileFieldMultiValue<F>(value),
							MediaType.APPLICATION_JSON_TYPE).put();
		}
	}

	/**
	 * Updates a single field on the profile of the user
	 */
	public <F> void updateProfileField(ProfileField<F, ?> field, F... values) {
		updateProfileField(field, Arrays.asList(values));
	}

	/**
	 * Updates a single field on the profile of the user
	 */
	public <F> void updateProfileField(ProfileField<F, ?> field, List<F> values) {
		if (field.isSingle()) {
			throw new IllegalArgumentException(
					"Field is only valid for single value");
		} else {
			baseAPI.getResource("/user/profile/" + field.getName())
					.entity(new ProfileFieldMultiValue<F>(values),
							MediaType.APPLICATION_JSON_TYPE).put();
		}
	}

	/**
	 * Updates the fields of an existing profile. Will only update the fields in
	 * the values.
	 */
	public void updateProfile(ProfileFieldValues values) {
		baseAPI.getResource("/user/profile/")
				.entity(values, MediaType.APPLICATION_JSON_TYPE).put();
	}

	/**
	 * Gets the active user
	 */
	public User getUser() {
		return baseAPI.getResource("/user/")
				.accept(MediaType.APPLICATION_JSON_TYPE).get(User.class);
	}

	/**
	 * Returns the user with given mail address.
	 */
	public User getUserByMail(String mail) {
		return baseAPI.getResource("/user/" + mail)
				.accept(MediaType.APPLICATION_JSON_TYPE).get(User.class);
	}

	/**
	 * Returns the value of the property for the active user with the given
	 * name. The property is specific to the auth client used.
	 */
	public boolean getProperty(String key) {
		return baseAPI.getResource("/user/property/" + key)
				.get(PropertyValue.class).getValue();
	}

	/**
	 * Sets the value of the property for the active user with the given name.
	 * The property is specific to the auth client used.
	 */
	public void setProperty(String key, boolean value) {
		baseAPI.getResource("/user/property/" + key)
				.entity(new PropertyValue(value),
						MediaType.APPLICATION_JSON_TYPE).put();
	}

	/**
	 * Deletes the property for the active user with the given name. The
	 * property is specific to the auth client used.
	 */
	public void deleteProperty(String key) {
		baseAPI.getResource("/user/property/" + key).delete();
	}
}
