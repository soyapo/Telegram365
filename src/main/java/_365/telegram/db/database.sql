--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-09-03 13:52:51

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
-- TOC entry 5018 (class 0 OID 0)
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
-- TOC entry 218 (class 1259 OID 24619)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    phone character varying(20) NOT NULL,
    username character varying(50) NOT NULL,
    bio text,
    profile_picture text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 5011 (class 0 OID 24736)
-- Dependencies: 224
-- Data for Name: channel_subscribers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.channel_subscribers (channel_id, user_id) FROM stdin;
\.


--
-- TOC entry 5010 (class 0 OID 24716)
-- Dependencies: 223
-- Data for Name: channels; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.channels (id, name, description, owner_id, is_public, pinned_message, created_at) FROM stdin;
\.


--
-- TOC entry 5009 (class 0 OID 24698)
-- Dependencies: 222
-- Data for Name: group_members; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.group_members (group_id, user_id, is_admin, is_muted, is_banned) FROM stdin;
\.


--
-- TOC entry 5008 (class 0 OID 24676)
-- Dependencies: 221
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groups (id, name, owner_id, is_public, invite_code, pinned_message, created_at) FROM stdin;
\.


--
-- TOC entry 5007 (class 0 OID 24663)
-- Dependencies: 220
-- Data for Name: media; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.media (id, message_id, file_name, file_type, file_path) FROM stdin;
\.


--
-- TOC entry 5012 (class 0 OID 24751)
-- Dependencies: 225
-- Data for Name: message_views; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message_views (message_id, user_id, viewed_at) FROM stdin;
\.


--
-- TOC entry 5006 (class 0 OID 24632)
-- Dependencies: 219
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, sender_id, receiver_id, receiver_type, content, message_type, reply_to, forwarded_from, forwarded_from_user, is_edited, edited_at, is_deleted, "timestamp") FROM stdin;
\.


--
-- TOC entry 5005 (class 0 OID 24619)
-- Dependencies: 218
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, phone, username, bio, profile_picture, created_at) FROM stdin;
\.


--
-- TOC entry 4842 (class 2606 OID 24740)
-- Name: channel_subscribers channel_subscribers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_pkey PRIMARY KEY (channel_id, user_id);


--
-- TOC entry 4840 (class 2606 OID 24725)
-- Name: channels channels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pkey PRIMARY KEY (id);


--
-- TOC entry 4838 (class 2606 OID 24705)
-- Name: group_members group_members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_pkey PRIMARY KEY (group_id, user_id);


--
-- TOC entry 4834 (class 2606 OID 24687)
-- Name: groups groups_invite_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_invite_code_key UNIQUE (invite_code);


--
-- TOC entry 4836 (class 2606 OID 24685)
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (id);


--
-- TOC entry 4832 (class 2606 OID 24670)
-- Name: media media_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_pkey PRIMARY KEY (id);


--
-- TOC entry 4844 (class 2606 OID 24756)
-- Name: message_views message_views_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_pkey PRIMARY KEY (message_id, user_id);


--
-- TOC entry 4830 (class 2606 OID 24642)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 4824 (class 2606 OID 24629)
-- Name: users users_phone_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_phone_key UNIQUE (phone);


--
-- TOC entry 4826 (class 2606 OID 24627)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4828 (class 2606 OID 24631)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4856 (class 2606 OID 24741)
-- Name: channel_subscribers channel_subscribers_channel_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_channel_id_fkey FOREIGN KEY (channel_id) REFERENCES public.channels(id) ON DELETE CASCADE;


--
-- TOC entry 4857 (class 2606 OID 24746)
-- Name: channel_subscribers channel_subscribers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channel_subscribers
    ADD CONSTRAINT channel_subscribers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4854 (class 2606 OID 24726)
-- Name: channels channels_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- TOC entry 4855 (class 2606 OID 24731)
-- Name: channels channels_pinned_message_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.channels
    ADD CONSTRAINT channels_pinned_message_fkey FOREIGN KEY (pinned_message) REFERENCES public.messages(id);


--
-- TOC entry 4852 (class 2606 OID 24706)
-- Name: group_members group_members_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE;


--
-- TOC entry 4853 (class 2606 OID 24711)
-- Name: group_members group_members_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4850 (class 2606 OID 24688)
-- Name: groups groups_owner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- TOC entry 4851 (class 2606 OID 24693)
-- Name: groups groups_pinned_message_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pinned_message_fkey FOREIGN KEY (pinned_message) REFERENCES public.messages(id);


--
-- TOC entry 4849 (class 2606 OID 24671)
-- Name: media media_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.media
    ADD CONSTRAINT media_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- TOC entry 4858 (class 2606 OID 24757)
-- Name: message_views message_views_message_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_message_id_fkey FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- TOC entry 4859 (class 2606 OID 24762)
-- Name: message_views message_views_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message_views
    ADD CONSTRAINT message_views_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4845 (class 2606 OID 24653)
-- Name: messages messages_forwarded_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_fkey FOREIGN KEY (forwarded_from) REFERENCES public.messages(id);


--
-- TOC entry 4846 (class 2606 OID 24658)
-- Name: messages messages_forwarded_from_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_user_fkey FOREIGN KEY (forwarded_from_user) REFERENCES public.users(id);


--
-- TOC entry 4847 (class 2606 OID 24648)
-- Name: messages messages_reply_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_reply_to_fkey FOREIGN KEY (reply_to) REFERENCES public.messages(id);


--
-- TOC entry 4848 (class 2606 OID 24643)
-- Name: messages messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id);


-- Completed on 2025-09-03 13:52:51

--
-- PostgreSQL database dump complete
--

