package lila
package forum

import scalaz.effects._
import com.github.ornicar.paginator._

final class TopicApi(env: ForumEnv, maxPerPage: Int) {

  def show(categSlug: String, slug: String, page: Int): IO[Option[(Categ, Topic, Paginator[Post])]] =
    get(categSlug, slug) map { 
      _ map {
        case (categ, topic) ⇒ (categ, topic, env.postApi.paginator(topic, page))
      }
    }

  def get(categSlug: String, slug: String) = for {
    categOption ← env.categRepo bySlug categSlug
    topicOption ← env.topicRepo.byTree(categSlug, slug)
  } yield (categOption |@| topicOption).tupled

  def paginator(categ: Categ, page: Int): Paginator[TopicView] =
    Paginator(
      SalatAdapter(
        dao = env.topicRepo,
        query = env.topicRepo byCategQuery categ,
        sort = env.topicRepo.sortQuery) map { topic ⇒
          env.postRepo byId topic.lastPostId map { post ⇒
            TopicView(categ, topic, post, env.postApi.pageOf)
          } unsafePerformIO
        },
      currentPage = page,
      maxPerPage = maxPerPage
    ) | paginator(categ, 1)

  def denormalize(topic: Topic): IO[Unit] = for {
    nbPosts ← env.postRepo countByTopics List(topic)
    lastPost ← env.postRepo lastByTopics List(topic)
    _ ← env.topicRepo.saveIO(topic.copy(
      nbPosts = nbPosts,
      lastPostId = lastPost.id
    ))
  } yield ()

  val denormalize: IO[Unit] = for {
    topics ← env.topicRepo.all
    _ ← topics.map(denormalize).sequence
  } yield ()
}
