package sem.hoa.domain.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleTest {

    @Test
    void getRuleId() {
        Rule rule = new Rule(100, 100, "desc");
        assertThat(rule.getRuleId()).isEqualTo(100);
    }

    @Test
    void getHoaID() {
        Rule rule = new Rule(1, "desc");
        assertThat(rule.getHoaID()).isEqualTo(1);
        assertThat(rule.getHoaID()).isNotNull();
    }

    @Test
    void getHoaID2() {
        Rule rule = new Rule(8137, "desc");
        assertThat(rule.getHoaID()).isEqualTo(8137);
        assertThat(rule.getHoaID()).isNotNull();
    }

    @Test
    void getDescription() {
        Rule rule = new Rule(8137, "desc");
        assertThat(rule.getDescription()).isEqualTo("desc");
        assertThat(rule.getHoaID()).isNotNull();
    }

    @Test
    void getDescription2() {
        Rule rule = new Rule(8137, "");
        assertThat(rule.getDescription()).isEqualTo("");
        assertThat(rule.getHoaID()).isNotNull();
    }

    @Test
    void setDescription() {
        Rule rule = new Rule(8137, "");
        assertThat(rule.getDescription()).isEqualTo("");
        rule.setDescription("1");
        assertThat(rule.getDescription()).isEqualTo("1");
    }

    @Test
    void setDescription2() {
        Rule rule = new Rule(8137, "1");
        assertThat(rule.getDescription()).isEqualTo("1");
        rule.setDescription("");
        assertThat(rule.getDescription()).isEqualTo("");
    }

    @Test
    void testEquals() {
        Rule rule = new Rule(1, "desc1");
        Rule rule2 = new Rule(1, "desc1");
        assertThat(rule.equals(rule2)).isTrue();
    }

    @Test
    void testEqualsEmptyDesc() {
        Rule rule = new Rule(1, "desc1");
        Rule rule2 = new Rule(1, "");
        assertThat(rule.equals(rule2)).isFalse();
    }

    @Test
    void testEqualsNull() {
        Rule rule1 = new Rule(1, "desc1");
        Rule rule2 = null;
        assertThat(rule1.equals(rule2)).isFalse();
    }

    @Test
    void testEqualsSameObject() {
        Rule rule = new Rule(1, "desc");
        assertThat(rule.equals(rule)).isTrue();
    }

    @Test
    void testHashCodeEqual() {
        Rule rule1 = new Rule(1, "desc1");
        Rule rule2 = new Rule(1, "desc1");
        assertThat(rule1.hashCode()).isEqualTo(rule2.hashCode());
    }

    @Test
    void testHashCodeNotEqual() {
        Rule rule1 = new Rule(1, "desc1");
        Rule rule2 = new Rule(2, "desc1");
        assertThat(rule1.hashCode()).isNotEqualTo(rule2.hashCode());
    }
}
