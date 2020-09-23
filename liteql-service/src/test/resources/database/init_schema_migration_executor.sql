create table liteql_test_migration_history(
  domain_type_name varchar(255) not null,
  version varchar(32) not null,
  description varchar(1000) null,
  state varchar(30) null
);

alter table liteql_test_migration_history
  add primary key (domain_type_name, version);

create table liteql_test_organization(
  id varchar(128) not null,
  name varchar(255) not null,
  code varchar(255) not null,
  parent_id varchar(255) not null,
  priority int not null,
  sort_code varchar(255) not null,
  leaf boolean not null,
  enabled boolean not null,
  deleted boolean not null,
  deletable boolean not null,
  inherent boolean not null,
  creator_id varchar(255) null,
  creator_name varchar(255) null,
  create_time timestamp null,
  last_modifier_id varchar(255) null,
  last_modifier_name varchar(255) null,
  last_modified_time timestamp null
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
  phone varchar(255) null,
  avatar_url varchar(255) null,
  organization_id varchar(128) null,
  enabled boolean not null,
  deleted boolean not null,
  deletable boolean not null,
  inherent boolean not null,
  creator_id varchar(255) null,
  creator_name varchar(255) null,
  create_time timestamp null,
  last_modifier_id varchar(255) null,
  last_modifier_name varchar(255) null,
  last_modified_time timestamp null
);

alter table liteql_test_user
  add constraint pk_liteql_test_user
    primary key (id);

alter table liteql_test_user
  add constraint uk_aeb1f5828b49215036bc
    unique (username);
