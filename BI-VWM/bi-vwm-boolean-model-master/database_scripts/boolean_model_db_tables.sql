create table article
(
    id int auto_increment
        primary key,
    article text null,
    link varchar(255) null,
    name varchar(255) null,
    constraint UK_9yh3uidjj6cnugskaogt626d5
        unique (name),
    constraint UK_oneuf1gn26d7hnunsn1oix6ju
        unique (link)
);

create index IDX_ARTICLE
    on article (id);

create table term
(
    id int auto_increment
        primary key,
    count int null,
    term varchar(255) null,
    constraint UK_pu7ldi1f67tpuyyoj8y5f20jp
        unique (term)
);

create index IDX_TERM
    on term (term);

create table article_term
(
    article_id int not null,
    term_id int not null,
    constraint FKbvpbck02cyk75wp7790b74aqc
        foreign key (article_id) references article (id),
    constraint FKhrn6nogvdckx2unt0sjjw2bhb
        foreign key (term_id) references term (id)
);
