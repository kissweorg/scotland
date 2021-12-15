package com.kisswe.scotland.client.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoProfileResponse {
    private Long id;
    private Boolean hasSignedUp;
    private LocalDateTime connectedAt;
    private LocalDateTime synchedAt;
    private KakaoAccount kakaoAccount;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoAccount {
        private Boolean profileNeedsAgreement;
        private Boolean profileNicknameNeedsAgreement;
        private Boolean profileImageNeedsAgreement;
        private Boolean nameNeedsAgreement;
        private String name;
        private Boolean emailNeedsAgreement;
        private Boolean isEmailValid;
        private Boolean isEmailVerified;
        private String email;
        private Boolean ageRangeNeedsAgreement;
        private String ageRange;
        private Boolean birthyearNeedsAgreement;
        private String birthyear;
        private Boolean birthdayNeedsAgreement;
        private String birthday;
        private String birthdayType;
        private Boolean genderNeedsAgreement;
        private String gender;
        private Boolean phoneNumberNeedsAgreement;
        private String phoneNumber;
        private Boolean ciNeedsAgreement;
        private String ci;
        private LocalDateTime ciAuthenticatedAt;
        private Profile profile;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Profile {
            private String nickname;
            private String thumbnailImageUrl;
            private String profileImageUrl;
            private Boolean isDefaultImage;
        }
    }


}
