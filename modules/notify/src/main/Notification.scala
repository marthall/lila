package lila.notify

import chess.Color
import lila.common.paginator.Paginator
import lila.notify.MentionedInThread.PostId
import org.joda.time.DateTime
import ornicar.scalalib.Random

case class NewNotification(notification: Notification, unreadNotifications: Int)

case class Notification(
    _id: String,
    notifies: Notification.Notifies,
    content: NotificationContent,
    read: Notification.NotificationRead,
    createdAt: DateTime) {
  def id = _id

  def unread = !read.value
}

object Notification {

  case class UnreadCount(value: Int) extends AnyVal
  case class AndUnread(pager: Paginator[Notification], unread: UnreadCount)
  case class Notifies(value: String) extends AnyVal with StringValue
  case class NotificationRead(value: Boolean) extends AnyVal

  def apply(notifies: Notification.Notifies, content: NotificationContent): Notification = {
    val idSize = 8
    val id = Random nextStringUppercase idSize
    new Notification(id, notifies, content, NotificationRead(false), DateTime.now)
  }
}

sealed trait NotificationContent
case class MentionedInThread(mentionedBy: MentionedInThread.MentionedBy,
  topic: MentionedInThread.Topic,
  topidId: MentionedInThread.TopicId,
  category: MentionedInThread.Category,
  postId: PostId) extends NotificationContent

object MentionedInThread {
  case class MentionedBy(value: String) extends AnyVal with StringValue
  case class Topic(value: String) extends AnyVal with StringValue
  case class TopicId(value: String) extends AnyVal with StringValue
  case class Category(value: String) extends AnyVal with StringValue
  case class PostId(value: String) extends AnyVal with StringValue
}

case class InvitedToStudy(invitedBy: InvitedToStudy.InvitedBy,
  studyName: InvitedToStudy.StudyName,
  studyId: InvitedToStudy.StudyId) extends NotificationContent

object InvitedToStudy {
  case class InvitedBy(value: String) extends AnyVal with StringValue
  case class StudyName(value: String) extends AnyVal with StringValue
  case class StudyId(value: String) extends AnyVal with StringValue
}

case class PrivateMessage(
  senderId: PrivateMessage.SenderId,
  thread: PrivateMessage.Thread,
  text: PrivateMessage.Text) extends NotificationContent

object PrivateMessage {
  case class SenderId(value: String) extends AnyVal with StringValue
  case class Thread(id: String, name: String)
  case class Text(value: String) extends AnyVal with StringValue
}

case class QaAnswer(
  answeredBy: QaAnswer.AnswererId,
  question: QaAnswer.Question,
  answerId: QaAnswer.AnswerId) extends NotificationContent

object QaAnswer {
  case class AnswererId(value: String) extends AnyVal with StringValue
  case class Question(id: Int, slug: String, title: String)
  case class AnswerId(value: Int) extends AnyVal
}

case class TeamJoined(id: TeamJoined.Id, name: TeamJoined.Name) extends NotificationContent

object TeamJoined {
  case class Id(value: String) extends AnyVal with StringValue
  case class Name(value: String) extends AnyVal with StringValue
}

case class NewBlogPost(
  id: NewBlogPost.Id,
  slug: NewBlogPost.Slug,
  title: NewBlogPost.Title) extends NotificationContent

object NewBlogPost {
  case class Id(value: String) extends AnyVal with StringValue
  case class Slug(value: String) extends AnyVal with StringValue
  case class Title(value: String) extends AnyVal with StringValue
}

case class AnalysisFinished(id: AnalysisFinished.Id, playedAs: Color, opponentName: AnalysisFinished.OpponentName) extends NotificationContent

object AnalysisFinished {
  case class Id(value: String) extends AnyVal with StringValue
  case class OpponentName(value: String) extends AnyVal with StringValue
}
