package cool.graph.api.database.mutactions.mutactions

import cool.graph.api.database.DatabaseMutationBuilder
import cool.graph.api.database.mutactions.{ClientSqlDataChangeMutaction, ClientSqlStatementResult}
import cool.graph.api.mutations.{NodeSelector, ParentInfo}
import cool.graph.shared.models._

import scala.concurrent.Future

case class AddDataItemToManyRelationByUniqueField(project: Project, parentInfo: ParentInfo, where: NodeSelector) extends ClientSqlDataChangeMutaction {

  val aModel: Model            = parentInfo.relation.getModelA_!(project.schema)
  val bModel: Model            = parentInfo.relation.getModelB_!(project.schema)
  val connectByUniqueValueForB = aModel.name == parentInfo.model.name

  override def execute: Future[ClientSqlStatementResult[Any]] = Future.successful {
    val action = if (connectByUniqueValueForB) {
      DatabaseMutationBuilder.createRelationRowByUniqueValueForB(project.id, parentInfo, where)
    } else {
      DatabaseMutationBuilder.createRelationRowByUniqueValueForA(project.id, parentInfo, where)
    }
    ClientSqlStatementResult(sqlAction = action)
  }
}
