package pl.ki.az.model.client;

public class UserId {
    private Long userId;

    public UserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;

        UserId userId1 = (UserId) o;

        return userId != null ? userId.equals(userId1.userId) : userId1.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
