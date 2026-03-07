export interface Message {
    username: string;
    text: string;
    pubDate: string;
    gravatarUrl: string;
    authorId: number;
}

export interface FollowsResponse {
    follows: string[];
}