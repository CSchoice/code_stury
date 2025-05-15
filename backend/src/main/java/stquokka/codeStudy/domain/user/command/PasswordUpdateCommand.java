package stquokka.codeStudy.domain.user.command;

public record PasswordUpdateCommand (
        String currentPassword,
        String newPassword
//        String email
){
}
