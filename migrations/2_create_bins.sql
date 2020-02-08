CREATE TABLE public.rb_bins
(
  id integer NOT NULL,
  user_id integer NOT NULL,
  name text NOT NULL,
  type text NOT NULL,
  active boolean NOT NULL,
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL,
  CONSTRAINT rb_bins_pkey PRIMARY KEY (id),
  CONSTRAINT rb_bins_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES public.rb_users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE SEQUENCE public.bin_id_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
