export interface Message {
    username: string;
    content: string;
    pub_date: string;
}

export interface FollowsResponse {
    follows: string[];
}

export interface UserProfileData {
    messages: Message[];
    followed: boolean;
    // Add any other user fields you return from Java
}