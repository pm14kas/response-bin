CREATE TABLE public.rb_requests
(
  id integer NOT NULL,
  bin_id integer NOT NULL,
  body text NOT NULL,
  header text NOT NULL,
  ip text NOT NULL,
  method text NOT NULL,
  commentary text,
  response text NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT rb_requests_pkey PRIMARY KEY (id),
  CONSTRAINT rb_requests_bin_id_fkey FOREIGN KEY (bin_id)
      REFERENCES public.rb_bins (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE SEQUENCE public.request_id_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

