package cognito.example.cognito_demo.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.util.List;

@Service
public class CognitoService {

    private static final String USER_POOL_ID = "us-east-1_kPfzTh4dA"; // ユーザープールID
    private static final String AKI = "";
    private static final String SAK = "";
    private static final Region REGION = Region.US_EAST_1; // リージョン

    private CognitoIdentityProviderClient createCognitoClient() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(AKI, SAK);
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(REGION)
                .build();
    }

    public boolean checkIfUserExists(String email) {
        try (CognitoIdentityProviderClient cognitoClient = createCognitoClient()) {
            ListUsersRequest request = ListUsersRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .filter("email = \"" + email + "\"")
                    .build();

            ListUsersResponse response = cognitoClient.listUsers(request);
            List<UserType> users = response.users();
            return !users.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
