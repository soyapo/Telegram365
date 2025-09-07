--
-- PostgreSQL database dump
--

\restrict 21WSYWQvJA4lWo5aEdZIigWwJx7I5UXTxcsDKctoAn7qX47OLzZTsMhajOcU7ge

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2025-09-07 02:57:31

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 226 (class 1259 OID 24846)
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id uuid NOT NULL,
    sender_id uuid,
    receiver_id uuid,
    chat_type character varying(10) NOT NULL,
    content text,
    message_type character varying(20),
    reply_to uuid,
    forwarded_from uuid,
    forwarded_from_user uuid,
    is_edited boolean DEFAULT false,
    edited_at timestamp without time zone,
    is_deleted boolean DEFAULT false,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    file_path text,
    CONSTRAINT messages_receiver_type_check CHECK (((chat_type)::text = ANY (ARRAY[('private'::character varying)::text, ('group'::character varying)::text, ('channel'::character varying)::text])))
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- TOC entry 4965 (class 0 OID 24846)
-- Dependencies: 226
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, sender_id, receiver_id, chat_type, content, message_type, reply_to, forwarded_from, forwarded_from_user, is_edited, edited_at, is_deleted, "timestamp", file_path) FROM stdin;
\.


--
-- TOC entry 4815 (class 2606 OID 24898)
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);


--
-- TOC entry 4816 (class 2606 OID 24984)
-- Name: messages messages_forwarded_from_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_fkey FOREIGN KEY (forwarded_from) REFERENCES public.messages(id);


--
-- TOC entry 4817 (class 2606 OID 24989)
-- Name: messages messages_forwarded_from_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_forwarded_from_user_fkey FOREIGN KEY (forwarded_from_user) REFERENCES public.users(id);


--
-- TOC entry 4818 (class 2606 OID 24994)
-- Name: messages messages_reply_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_reply_to_fkey FOREIGN KEY (reply_to) REFERENCES public.messages(id);


--
-- TOC entry 4819 (class 2606 OID 24999)
-- Name: messages messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(id);


-- Completed on 2025-09-07 02:57:31

--
-- PostgreSQL database dump complete
--

\unrestrict 21WSYWQvJA4lWo5aEdZIigWwJx7I5UXTxcsDKctoAn7qX47OLzZTsMhajOcU7ge

