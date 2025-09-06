--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-09-06 22:16:00

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 24582)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 5064 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 24736)
-- Name: channel_subscribers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.channel_subscribers (
    channel_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.channel_subscribers OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 24716)
-- Name: channels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.channels (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    description text,
    owner_id uuid,
    is_public boolean DEFAULT true,
    pinned_message uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.channels OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 24767)
-- Name: contacts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contacts (
    owner_user_id uuid NOT NULL,
    contact_user_id uuid NOT NULL,
    first_name_alias character varying(50),
    last_name_alias character varying(50),
    added_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.contacts OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 24698)
-- Name: group_members; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.group_members (
    group_id uuid NOT NULL,
    user_id uuid NOT NULL,
    is_admin boolean DEFAULT false,
    is_muted boolean DEFAULT false,
    is_banned boolean DEFAULT false
);


ALTER TABLE public.group_members OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24676)
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    owner_id uuid,
    is_public boolean DEFAULT true,
    invite_code character varying(20),
    pinned_message uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 24663)
-- Name: media; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.media (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    message_id uuid,
    file_name text,
    file_type text,
    file_path text
);


ALTER TABLE public.media OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 24812)
-- Name: message_reactions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.message_reactions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    message_id uuid NOT NULL,
    user_id uuid NOT NULL,
    reaction text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.message_reactions OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24751)
-- Name: message_views; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.message_views (
    message_id uuid NOT NULL,
    user_id uuid NOT NULL,
    viewed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.message_views OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24632)
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id uuid NOT NULL,
    sender_id uuid,
    receiver_id uuid,
    receiver_type character varying(10) NOT NULL,
    content text,
    message_type character varying(20),
    reply_to uuid,
    forwarded_from uuid,
    forwarded_from_user uuid,
    is_edited boolean DEFAULT false,
    edited_at timestamp without time zone,
    is_deleted boolean DEFAULT false,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT messages_receiver_type_check CHECK (((receiver_type)::text = ANY ((ARRAY['private'::character varying, 'group'::character varying, 'channel'::character varying])::text[])))
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 24798)
-- Name: user_chat_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_chat_metadata (
    user_id uuid NOT NULL,
    chat_id uuid NOT NULL,
    chat_type character varying(10) NOT NULL,
    last_read_message_id uuid,
    is_archived boolean DEFAULT false,
    is_pinned boolean DEFAULT false,
    notification_settings text
);


ALTER TABLE public.user_chat_metadata OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24783)
-- Name: user_settings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_settings (
    user_id uuid NOT NULL,
    last_seen_privacy character varying(20) DEFAULT 'everyone'::character varying NOT NULL,
    profile_photo_privacy character varying(20) DEFAULT 'everyone'::character varying NOT NULL,
    calls_privacy character varying(20) DEFAULT 'everyone'::character varying NOT NULL,
    global_notifications boolean DEFAULT true
);


ALTER TABLE public.user_settings OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 24619)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    phone character varying(20) NOT NULL,
    username character varying(50) NOT NULL,
    bio text,
    profile_picture text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_seen timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 5053 (class 0 OID 24736)
-- Dependencies: 224
-- Data for Name: channel_subscribers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.channel_subscribers (channel_id, user_id) FROM stdin;
\.


--
-- TOC entry 5052 (class 0 OID 24716)
-- Dependencies: 223
-- Data for Name: channels; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.channels (id, name, description, owner_id, is_public, pinned_message, created_at) FROM stdin;
\.


--
-- TOC entry 5055 (class 0 OID 24767)
-- Dependencies: 226
-- Data for Name: contacts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contacts (owner_user_id, contact_user_id, first_name_alias, last_name_alias, added_at) FROM stdin;
\.


--
-- TOC entry 5051 (class 0 OID 24698)
-- Dependencies: 222
-- Data for Name: group_members; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.group_members (group_id, user_id, is_admin, is_muted, is_banned) FROM stdin;
\.


--
-- TOC entry 5050 (class 0 OID 24676)
-- Dependencies: 221
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groups (id, name, owner_id, is_public, invite_code, pinned_message, created_at) FROM stdin;
\.


--
-- TOC entry 5049 (class 0 OID 24663)
-- Dependencies: 220
-- Data for Name: media; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.media (id, message_id, file_name, file_type, file_path) FROM stdin;
\.


--
-- TOC entry 5058 (class 0 OID 24812)
-- Dependencies: 229
-- Data for Name: message_reactions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message_reactions (id, message_id, user_id, reaction, created_at) FROM stdin;
\.


--
-- TOC entry 5054 (class 0 OID 24751)
-- Dependencies: 225
-- Data for Name: message_views; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message_views (message_id, user_id, viewed_at) FROM stdin;
\.


--
-- TOC entry 5048 (class 0 OID 24632)
-- Dependencies: 219
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, sender_id, receiver_id, receiver_type, content, message_type, reply_to, forwarded_from, forwarded_from_user, is_edited, edited_at, is_deleted, "timestamp") FROM stdin;
\.


--
-- TOC entry 5057 (class 0 OID 24798)
-- Dependencies: 228
-- Data for Name: user_chat_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_chat_metadata (user_id, chat_id, chat_type, last_read_message_id, is_archived, is_pinned, notification_settings) FROM stdin;
\.


--
-- TOC entry 5056 (class 0 OID 24783)
-- Dependencies: 227
-- Data for Name: user_settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_settings (user_id, last_seen_privacy, profile_photo_privacy, calls_privacy, global_notifications) FROM stdin;
\.


--
-- TOC entry 5047 (class 0 OID 24619)
-- Dependencies: 218
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, phone, username, bio, profile_picture, created_at, last_seen) FROM stdin;
c15fc899-3c35-44c7-b895-a0a588d5db99	9053960085	kjoi	Bio (Optional)	\N	2025-09-06 02:51:10.467878	2025-09-06 16:05:16.359499
8ce419c9-2290-42a9-80af-96b035bd396a	9215814101	soso	Bio (Optional)	\N	2025-09-06 03:41:03.347205	2025-09-06 16:05:16.359499
b559bc8a-e983-45f8-a15e-2c08284f9fa8	9215914101	Salam	Bio (Optional)	\N	2025-09-06 03:42:43.812727	2025-09-06 16:05:16.359499
c309ac37-7656-4850-bc03-35f4e82a985c	9898787878	reuhihihlhllihlh	Bio (Optional)	\N	2025-09-06 04:11:02.146398	2025-09-06 16:05:16.359499
7dbc2e07-e439-4faf-b232-49f8198ec23d	9677565656	klkjlj	Bio (Optional)	\N	2025-09-06 04:18:40.123598	2025-09-06 16:05:16.359499
6d237826-c823-45b4-93da-2ffb0e4ef937	9078978787	uhiuh	Bio (Optional)	\N	2025-09-06 04:34:23.787794	2025-09-06 16:05:16.359499
576ee186-f892-4a15-950f-100fa600e2db	9897867676	khuihh	Bio (Optional)	\N	2025-09-06 04:41:27.799818	2025-09-06 16:05:16.359499
4b398282-6ed9-445e-b3cb-26acf9bf82ed	9688676565	joihkjo	Bio (Optional)	\N	2025-09-06 04:43:33.819593	2025-09-06 16:05:16.359499
1f40b70e-b3e9-4d03-9c1c-530594010157	9566544343	kugig	Bio (Optional)	\N	2025-09-06 04:49:16.977165	2025-09-06 16:05:16.359499
f758ab37-69e0-47b5-a920-d9c184d3af45	9111111111	kofpwe	Bio (Optional)	\N	2025-09-06 22:03:03.381	2025-09-06 22:03:03.381
593b218d-f5b0-40fa-810d-6be2350fa9b8	9222222222	kiaaaaaa	Bio (Optional)	\N	2025-09-06 22:12:11.607882	2025-09-06 22:12:11.607882
\.


--
-- TOC entry 4868 (class 2606 OID 24740)
-- Name: channel_subscribers channel_subscribers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_pkey PRIMARY KEY (channel_id, user_id);


--
-- TOC entry 4866 (class 2606 OID 24725)
-- Name: channels channels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pkey PRIMARY KEY (id);


--
-- TOC entry 4872 (class 2606 OID 24772)
-- Name: contacts contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contacts
    ADD CONSTRAINT contacts_pkey PRIMARY KEY (owner_user_id, contact_user_id);


--
-- TOC entry 4864 (class 2606 OID 24705)
-- Name: group_members group_members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_pkey PRIMARY KEY (group_id, user_id);


--
-- TOC entry 4860 (class 2606 OID 24687)
-- Name: groups groups_invite_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_invite_code_key UNIQUE (invite_code);


--
-- TOC entry 4862 (class 2606 OID 24685)
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- TOC entry 4858 (class 2606 OID 24670)
-- Name: media media_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_pkey PRIMARY KEY (id);


--
-- TOC entry 4878 (class 2606 OID 24822)
-- Name: message_reactions message_reactions_message_id_user_id_reaction_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_reactions
    ADD CONSTRAINT message_reactions_message_id_user_id_reaction_key UNIQUE (message_id, user_id, reaction);


--
-- TOC entry 4880 (class 2606 OID 24820)
-- Name: message_reactions message_reactions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_reactions
    ADD CONSTRAINT message_reactions_pkey PRIMARY KEY (id);


--
-- TOC entry 4870 (class 2606 OID 24756)
-- Name: message_views message_views_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_pkey PRIMARY KEY (message_id, user_id);


--
-- TOC entry 4856 (class 2606 OID 24642)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 4876 (class 2606 OID 24806)
-- Name: user_chat_metadata user_chat_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_chat_metadata
    ADD CONSTRAINT user_chat_metadata_pkey PRIMARY KEY (user_id, chat_id);


--
-- TOC entry 4874 (class 2606 OID 24791)
-- Name: user_settings user_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT user_settings_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4850 (class 2606 OID 24629)
-- Name: users users_phone_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_key UNIQUE (phone);


--
-- TOC entry 4852 (class 2606 OID 24627)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4854 (class 2606 OID 24631)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4892 (class 2606 OID 24741)
-- Name: channel_subscribers channel_subscribers_channel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_channel_id_fkey FOREIGN KEY (channel_id) REFERENCES public.channels(id) ON DELETE CASCADE;


--
-- TOC entry 4893 (class 2606 OID 24746)
-- Name: channel_subscribers channel_subscribers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4890 (class 2606 OID 24726)
-- Name: channels channels_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- TOC entry 4891 (class 2606 OID 24731)
-- Name: channels channels_pinned_message_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pinned_message_fkey FOREIGN KEY (pinned_message) REFERENCES public.messages(id);


--
-- TOC entry 4896 (class 2606 OID 24778)
-- Name: contacts contacts_contact_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contacts
    ADD CONSTRAINT contacts_contact_user_id_fkey FOREIGN KEY (contact_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4897 (class 2606 OID 24773)
-- Name: contacts contacts_owner_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contacts
    ADD CONSTRAINT contacts_owner_user_id_fkey FOREIGN KEY (owner_user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4888 (class 2606 OID 24706)
-- Name: group_members group_members_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE;


--
-- TOC entry 4889 (class 2606 OID 24711)
-- Name: group_members group_members_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4886 (class 2606 OID 24688)
-- Name: groups groups_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- TOC entry 4887 (class 2606 OID 24693)
-- Name: groups groups_pinned_message_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pinned_message_fkey FOREIGN KEY (pinned_message) REFERENCES public.messages(id);


--
-- TOC entry 4885 (class 2606 OID 24671)
-- Name: media media_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- TOC entry 4900 (class 2606 OID 24823)
-- Name: message_reactions message_reactions_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_reactions
    ADD CONSTRAINT message_reactions_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- TOC entry 4901 (class 2606 OID 24828)
-- Name: message_reactions message_reactions_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_reactions
    ADD CONSTRAINT message_reactions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4894 (class 2606 OID 24757)
-- Name: message_views message_views_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- TOC entry 4895 (class 2606 OID 24762)
-- Name: message_views message_views_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4881 (class 2606 OID 24653)
-- Name: messages messages_forwarded_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_fkey FOREIGN KEY (forwarded_from) REFERENCES public.messages(id);


--
-- TOC entry 4882 (class 2606 OID 24658)
-- Name: messages messages_forwarded_from_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_user_fkey FOREIGN KEY (forwarded_from_user) REFERENCES public.users(id);


--
-- TOC entry 4883 (class 2606 OID 24648)
-- Name: messages messages_reply_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_reply_to_fkey FOREIGN KEY (reply_to) REFERENCES public.messages(id);


--
-- TOC entry 4884 (class 2606 OID 24643)
-- Name: messages messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id);


--
-- TOC entry 4899 (class 2606 OID 24807)
-- Name: user_chat_metadata user_chat_metadata_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_chat_metadata
    ADD CONSTRAINT user_chat_metadata_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4898 (class 2606 OID 24792)
-- Name: user_settings user_settings_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT user_settings_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


-- Completed on 2025-09-06 22:16:00

--
-- PostgreSQL database dump complete
--

