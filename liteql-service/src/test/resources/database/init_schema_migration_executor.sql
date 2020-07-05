create table liteql_test_schema_version(
  version varchar(32) not null,
  description varchar(1000) null,
  state varchar(30) null
);

alter table liteql_test_schema_version
  add primary key (version);

create table liteql_test_country(
  id varchar(128) not null,
  name varchar(255) not null,
  code varchar(255) not null
);

alter table liteql_test_country
  add constraint pk_liteql_test_country
    primary key (id);

 alter table liteql_test_country
  add constraint uk_2d239fcb4476b3df595c
    unique (code);

create table liteql_test_organization(
  id varchar(128) not null,
  name varchar(255) not null,
  code varchar(255) not null
);

alter table liteql_test_organization
  add constraint pk_liteql_organization
    primary key (id);

alter table liteql_test_organization
  add constraint uk_324f04f9c7f4d7f6ee4a
    unique (code);

create table liteql_test_user(
  id varchar(128) not null,
  name varchar(255) not null,
  username varchar(255) not null,
  email varchar(255) null,
  age integer null,
  organization_id varchar(128) null
);

alter table liteql_test_user
  add constraint pk_liteql_test_user
    primary key (id);

alter table liteql_test_user
  add constraint uk_aeb1f5828b49215036bc
    unique (username);
