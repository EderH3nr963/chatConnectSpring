package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClerkUserDataDTO(
        String id,
        String object,
        String username,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("profile_image_url") String profileImageUrl,
        @JsonProperty("primary_email_address_id") String primaryEmailAddressId,
        @JsonProperty("email_addresses") List<ClerkEmailAddressDTO> emailAddresses,
        @JsonProperty("created_at") Long createdAt,
        @JsonProperty("updated_at") Long updatedAt,
        @JsonProperty("banned") Boolean banned,
        @JsonProperty("locked") Boolean locked,
        @JsonProperty("public_metadata") Map<String, Object> publicMetadata,
        @JsonProperty("private_metadata") Map<String, Object> privateMetadata,
        @JsonProperty("unsafe_metadata") Map<String, Object> unsafeMetadata
) {
    public String getPrimaryEmail() {
        if (emailAddresses == null || emailAddresses.isEmpty()) {
            return null;
        }
        if (primaryEmailAddressId != null) {
            for (ClerkEmailAddressDTO email : emailAddresses) {
                if (primaryEmailAddressId.equals(email.id())) {
                    return email.emailAddress();
                }
            }
        }
        return emailAddresses.get(0).emailAddress();
    }

    public String resolveUsername() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        if (firstName != null && !firstName.isBlank()) {
            if (lastName != null && !lastName.isBlank()) {
                return firstName + " " + lastName;
            }
            return firstName;
        }
        String primaryEmail = getPrimaryEmail();
        if (primaryEmail != null && primaryEmail.contains("@")) {
            return primaryEmail.substring(0, primaryEmail.indexOf('@'));
        }
        return id;
    }
}
