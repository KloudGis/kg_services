package org.kloudgis.core.pojo.space;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

public class Transaction {

    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    public Long trx_id;
    public Long user_id;
    public String author;
    public Long parent_trx_id;
    public Long feature_id;
    public Timestamp time;
    public Integer trx_type;
    public String source;
    public List<TransactionAttribute> details;
    public Long ft_id;

    public Transaction() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("id=");
        buf.append(trx_id);
        buf.append(", ft_id=");
        buf.append(ft_id);
        buf.append(", fid=");
        buf.append(feature_id);
        buf.append(", Details:\n");
        buf.append(details);

        return buf.toString();
    }

    public static Transaction fromJSON(String json) throws IOException {
        return mapper.readValue(json, Transaction.class);
    }

    public String toJSON() throws IOException {
        return new String(mapper.writeValueAsBytes(this), "UTF-8");
    }

    public static Transaction fromMap(Map map) throws IOException {
        return mapper.convertValue(map, Transaction.class);
    }

    public Map toMap() throws IOException {
        return mapper.convertValue(this, HashMap.class);
    }
}
