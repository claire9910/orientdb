/* Generated By:JJTree: Do not edit this line. OContainsAnyCondition.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.common.collection.OMultiValue;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.executor.OIndexSearchInfo;
import com.orientechnologies.orient.core.sql.executor.OResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OContainsAnyCondition extends OBooleanExpression {

  protected OExpression left;

  protected OExpression right;

  protected OOrBlock rightBlock;

  public OContainsAnyCondition(int id) {
    super(id);
  }

  public OContainsAnyCondition(OrientSql p, int id) {
    super(p, id);
  }

  public boolean execute(Object left, Object right) {
    if (left instanceof Collection) {
      if (right instanceof Iterable) {
        right = ((Iterable) right).iterator();
      }
      if (right instanceof Iterator) {
        Iterator iterator = (Iterator) right;
        while (iterator.hasNext()) {
          Object next = iterator.next();
          if (((Collection) left).contains(next)) {
            return true;
          }
          if (next instanceof OResult
              && ((OResult) next).isElement()
              && ((Collection) left).contains(((OResult) next).toElement())) {
            return true;
          }
        }
      }
      return false;
    }
    if (left instanceof Iterable) {
      left = ((Iterable) left).iterator();
    }
    if (left instanceof Iterator) {
      if (!(right instanceof Iterable)) {
        right = Collections.singleton(right);
      }
      right = ((Iterable) right).iterator();

      Iterator leftIterator = (Iterator) left;
      Iterator rightIterator = (Iterator) right;
      while (rightIterator.hasNext()) {
        Object leftItem = rightIterator.next();
        while (leftIterator.hasNext()) {
          Object rightItem = leftIterator.next();
          if (leftItem != null && leftItem.equals(rightItem)) {
            return true;
          }
          Object leftElem =
              leftItem instanceof OResult && ((OResult) leftItem).isElement()
                  ? ((OResult) leftItem).getElement().get()
                  : rightItem;
          Object rightElem =
              rightItem instanceof OResult && ((OResult) rightItem).isElement()
                  ? ((OResult) rightItem).getElement().get()
                  : rightItem;
          if (leftElem != null && leftElem.equals(rightElem)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean evaluate(OIdentifiable currentRecord, OCommandContext ctx) {
    Object leftValue = left.execute(currentRecord, ctx);
    if (right != null) {
      Object rightValue = right.execute(currentRecord, ctx);
      return execute(leftValue, rightValue);
    } else {
      if (!OMultiValue.isMultiValue(leftValue)) {
        return false;
      }
      Iterator<Object> iter = OMultiValue.getMultiValueIterator(leftValue);
      while (iter.hasNext()) {
        Object item = iter.next();
        if (item instanceof OIdentifiable) {
          if (!rightBlock.evaluate((OIdentifiable) item, ctx)) {
            return false;
          }
        } else if (item instanceof OResult) {
          if (!rightBlock.evaluate((OResult) item, ctx)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
  }

  @Override
  public boolean evaluate(OResult currentRecord, OCommandContext ctx) {
    Object leftValue = left.execute(currentRecord, ctx);
    if (right != null) {
      Object rightValue = right.execute(currentRecord, ctx);
      return execute(leftValue, rightValue);
    } else {
      if (!OMultiValue.isMultiValue(leftValue)) {
        return false;
      }
      Iterator<Object> iter = OMultiValue.getMultiValueIterator(leftValue);
      while (iter.hasNext()) {
        Object item = iter.next();
        if (item instanceof OIdentifiable) {
          if (!rightBlock.evaluate((OIdentifiable) item, ctx)) {
            return false;
          }
        } else if (item instanceof OResult) {
          if (!rightBlock.evaluate((OResult) item, ctx)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    }
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    left.toString(params, builder);
    builder.append(" CONTAINSANY ");
    if (right != null) {
      right.toString(params, builder);
    } else if (rightBlock != null) {
      builder.append("(");
      rightBlock.toString(params, builder);
      builder.append(")");
    }
  }

  public OExpression getLeft() {
    return left;
  }

  public void setLeft(OExpression left) {
    this.left = left;
  }

  public OExpression getRight() {
    return right;
  }

  public void setRight(OExpression right) {
    this.right = right;
  }

  @Override
  public boolean supportsBasicCalculation() {
    if (left != null && !left.supportsBasicCalculation()) {
      return false;
    }
    if (right != null && !right.supportsBasicCalculation()) {
      return false;
    }
    if (rightBlock != null && !rightBlock.supportsBasicCalculation()) {
      return false;
    }
    return true;
  }

  @Override
  protected int getNumberOfExternalCalculations() {
    int total = 0;
    if (left != null && !left.supportsBasicCalculation()) {
      total++;
    }
    if (right != null && !right.supportsBasicCalculation()) {
      total++;
    }
    if (rightBlock != null && !rightBlock.supportsBasicCalculation()) {
      total++;
    }
    return total;
  }

  @Override
  protected List<Object> getExternalCalculationConditions() {
    List<Object> result = new ArrayList<Object>();
    if (left != null && !left.supportsBasicCalculation()) {
      result.add(left);
    }
    if (right != null && !right.supportsBasicCalculation()) {
      result.add(right);
    }
    if (rightBlock != null) {
      result.addAll(rightBlock.getExternalCalculationConditions());
    }
    return result;
  }

  @Override
  public boolean needsAliases(Set<String> aliases) {
    if (left.needsAliases(aliases)) {
      return true;
    }

    if (right != null && right.needsAliases(aliases)) {
      return true;
    }
    if (rightBlock != null && rightBlock.needsAliases(aliases)) {
      return true;
    }
    return false;
  }

  @Override
  public OContainsAnyCondition copy() {
    OContainsAnyCondition result = new OContainsAnyCondition(-1);
    result.left = left.copy();
    result.right = right == null ? null : right.copy();
    result.rightBlock = rightBlock == null ? null : rightBlock.copy();
    return result;
  }

  @Override
  public void extractSubQueries(SubQueryCollector collector) {
    left.extractSubQueries(collector);
    if (right != null) {
      right.extractSubQueries(collector);
    }
    if (rightBlock != null) {
      rightBlock.extractSubQueries(collector);
    }
  }

  @Override
  public boolean refersToParent() {
    if (left != null && left.refersToParent()) {
      return true;
    }
    if (right != null && right.refersToParent()) {
      return true;
    }
    if (rightBlock != null && rightBlock.refersToParent()) {
      return true;
    }
    return false;
  }

  @Override
  public OBooleanExpression rewriteIndexChainsAsSubqueries(OCommandContext ctx, OClass clazz) {
    if (right.isEarlyCalculated(ctx) && left.isIndexChain(ctx, clazz)) {
      OContainsAnyCondition result = new OContainsAnyCondition(-1);

      result.left = new OExpression(-1);
      OBaseExpression base = new OBaseExpression(-1);
      base.identifier = new OBaseIdentifier(-1);
      base.identifier.suffix = new OSuffixIdentifier(-1);
      base.identifier.suffix.identifier =
          ((OBaseExpression) left.mathExpression).identifier.suffix.identifier;
      result.left.mathExpression = base;

      OClass nextClazz =
          clazz.getProperty(base.identifier.suffix.identifier.getStringValue()).getLinkedClass();
      OParenthesisExpression newRight = new OParenthesisExpression(-1);
      newRight.statement =
          OBinaryCondition.indexChainToStatement(
              ((OBaseExpression) left.mathExpression).modifier, nextClazz, right, ctx);
      result.right = new OExpression(-1);
      result.right.mathExpression = newRight;
      return result;
    }
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OContainsAnyCondition that = (OContainsAnyCondition) o;

    if (left != null ? !left.equals(that.left) : that.left != null) return false;
    if (right != null ? !right.equals(that.right) : that.right != null) return false;
    if (rightBlock != null ? !rightBlock.equals(that.rightBlock) : that.rightBlock != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = left != null ? left.hashCode() : 0;
    result = 31 * result + (right != null ? right.hashCode() : 0);
    result = 31 * result + (rightBlock != null ? rightBlock.hashCode() : 0);
    return result;
  }

  @Override
  public List<String> getMatchPatternInvolvedAliases() {
    List<String> leftX = left == null ? null : left.getMatchPatternInvolvedAliases();
    List<String> rightX = right == null ? null : right.getMatchPatternInvolvedAliases();
    List<String> rightBlockX =
        rightBlock == null ? null : rightBlock.getMatchPatternInvolvedAliases();

    List<String> result = new ArrayList<String>();
    if (leftX != null) {
      result.addAll(leftX);
    }
    if (rightX != null) {
      result.addAll(rightX);
    }
    if (rightBlockX != null) {
      result.addAll(rightBlockX);
    }

    return result.size() == 0 ? null : result;
  }

  @Override
  public boolean isCacheable() {
    if (left != null && !left.isCacheable()) {
      return false;
    }

    if (right != null && !right.isCacheable()) {
      return false;
    }

    if (rightBlock != null && !rightBlock.isCacheable()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isIndexAware(OIndexSearchInfo info) {
    if (left.isBaseIdentifier()) {
      if (info.getField().equals(left.getDefaultAlias().getStringValue())) {
        if (right.isEarlyCalculated(info.getCtx())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public OExpression resolveKeyFrom(OBinaryCondition additional) {
    if (getRight() != null) {
      return getRight();
    } else {
      throw new UnsupportedOperationException("Cannot execute index query with " + this);
    }
  }

  @Override
  public OExpression resolveKeyTo(OBinaryCondition additional) {
    if (getRight() != null) {
      return getRight();
    } else {
      throw new UnsupportedOperationException("Cannot execute index query with " + this);
    }
  }
}
/* JavaCC - OriginalChecksum=7992ab9e8e812c6d9358ede8b67b4506 (do not edit this line) */
