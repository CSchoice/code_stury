package stquokka.codeStudy.domain.auth.command;

public record LoginCommand (
        String userId,
        String password
)
{}

