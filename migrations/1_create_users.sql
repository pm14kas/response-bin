CREATE TABLE public.rb_users
(
  id integer NOT NULL,
  email text NOT NULL,
  password text NOT NULL,
  scope text,
  created_at timestamp without time zone,
  updated_at timestamp without time zone,
  active boolean NOT NULL DEFAULT true,
  last_login timestamp without time zone,
  CONSTRAINT rb_users_pkey PRIMARY KEY (id)
);
CREATE SEQUENCE public.user_id_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
