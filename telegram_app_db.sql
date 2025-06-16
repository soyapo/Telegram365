

-- Table Users
CREATE TABLE Users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    profile_name VARCHAR(100),
    profile_picture_path VARCHAR(255),
    status VARCHAR(20) DEFAULT 'Offline'
);

-- Table GroupChats
CREATE TABLE GroupChats (
    group_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_name VARCHAR(100) NOT NULL,
    group_creator_id UUID NOT NULL,
    FOREIGN KEY (group_creator_id) REFERENCES Users(user_id)
);

-- Table GroupMembers
CREATE TABLE GroupMembers (
    group_id UUID NOT NULL,
    member_user_id UUID NOT NULL,
    PRIMARY KEY (group_id, member_user_id),
    FOREIGN KEY (group_id) REFERENCES GroupChats(group_id),
    FOREIGN KEY (member_user_id) REFERENCES Users(user_id)
);

-- Table Channels
CREATE TABLE Channels (
    channel_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel_name VARCHAR(100) NOT NULL,
    channel_owner_id UUID NOT NULL,
    FOREIGN KEY (channel_owner_id) REFERENCES Users(user_id)
);

-- Table ChannelSubscribers
CREATE TABLE ChannelSubscribers (
    channel_id UUID NOT NULL,
    subscriber_user_id UUID NOT NULL,
    PRIMARY KEY (channel_id, subscriber_user_id),
    FOREIGN KEY (channel_id) REFERENCES Channels(channel_id),
    FOREIGN KEY (subscriber_user_id) REFERENCES Users(user_id)
);

-- Table PrivateChats
CREATE TABLE PrivateChats (
    chat_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user1_id UUID NOT NULL,
    user2_id UUID NOT NULL,
    FOREIGN KEY (user1_id) REFERENCES Users(user_id),
    FOREIGN KEY (user2_id) REFERENCES Users(user_id),
    CONSTRAINT unique_private_chat UNIQUE (LEAST(user1_id, user2_id), GREATEST(user1_id, user2_id))
);

-- Table Messages
CREATE TABLE Messages (
    message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID NOT NULL,
    receiver_user_id UUID,
    receiver_group_id UUID,
    receiver_channel_id UUID,
    content TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    read_status VARCHAR(20) DEFAULT 'Sent',

    FOREIGN KEY (sender_id) REFERENCES Users(user_id),
    FOREIGN KEY (receiver_user_id) REFERENCES Users(user_id),
    FOREIGN KEY (receiver_group_id) REFERENCES GroupChats(group_id),
    FOREIGN KEY (receiver_channel_id) REFERENCES Channels(channel_id),

    CONSTRAINT one_receiver_type CHECK (
        (receiver_user_id IS NOT NULL AND receiver_group_id IS NULL AND receiver_channel_id IS NULL) OR
        (receiver_user_id IS NULL AND receiver_group_id IS NOT NULL AND receiver_channel_id IS NULL) OR
        (receiver_user_id IS NULL AND receiver_group_id IS NULL AND receiver_channel_id IS NOT NULL)
    )
);