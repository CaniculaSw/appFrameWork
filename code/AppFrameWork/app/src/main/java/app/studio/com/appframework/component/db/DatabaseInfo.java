package app.studio.com.appframework.component.db;

import java.util.List;

public class DatabaseInfo
{

    /**
     * 数据库的版本号
     * name = "version"
     */
    private String version;

    /**
     * 数据库的名称
     * name = "name"
     */
    private String name;


    /**
     * 数据库中的所有表
     * name = "table"
     */
    private List<Table> listTable;

    public String getVersion()
    {
        return version;
    }

    public List<Table> getListTable()
    {
        return listTable;
    }

    public void setListTable(List<Table> listTable)
    {
        this.listTable = listTable;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * 用于定义数据库表结构
     * name = "table"
     */
    public static class Table
    {

        /**
         * 数据库表名称
         * name = "name"
         */
        private String name;

        /**
         * name = "field"
         */
        private List<Field> listFiled;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public List<Field> getListFiled()
        {
            return listFiled;
        }

        public void setListFiled(List<Field> listFiled)
        {
            this.listFiled = listFiled;
        }

    }

    /**
     * 用于描述数据库表结构中的字段
     * name = "field"
     */
    public static class Field
    {
        /**
         * 字段名称
         * name = "name"
         */
        private String name;

        /**
         * 字段类型。Sqlite3支持的数据库字段类型。可为：text,integer,real,blob
         * name = "type"
         */
        private String type;

        /**
         * 描述字段的属性约束。约束为可为下列值的组合: ”primary key”,” autoincrement”,” not
         * null”,” unique”,” default”。 这些字段的含义如下： primary key为主键；
         * autoincrement为自增长； not null为非空； unique为唯一；
         * default为默认值。default后需跟默认的值，但不能有CURRENT_TIME, CURRENT_DATE
         * 或CURRENT_TIMESTAMP等默认值。 若定义了NOT NULL约束，则字段必须有一个非空的缺省值。
         * name = "obligatory"
         */
        private String obligatory;

        public String getObligatory()
        {
            return obligatory;
        }

        public void setObligatory(String obligatory)
        {
            this.obligatory = obligatory;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }


    /**
     * 数据库版本号表 列名字段描述
     */
    public interface GlobalDbVer
    {
        public static final String TABLE_NAME = "commondbVer";
        /**
         * 唯一标识
         */
        public static final String TABLE_ID = "_ID";

        /**
         * 数据库的版本信息
         */
        public static final String TABLE_DB_VER = "dbVer";

        /**
         * 数据库表结构的描述信息
         */
        public static final String TABLE_DESC = "desc";
    }
}
